package com.proyecto.Controller;

import com.proyecto.Domain.Carrito;
import com.proyecto.Service.VentaService;
import com.proyecto.Service.ProductoService;
import com.proyecto.Domain.Producto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@Controller
public class CarritoController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    // Mostrar el carrito de compras
    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {
        try {
            // Obtener carrito de la sesión
            @SuppressWarnings("unchecked")
            List<Carrito> carritoItems = (List<Carrito>) session.getAttribute("carrito");

            if (carritoItems == null) {
                carritoItems = new ArrayList<>();
                session.setAttribute("carrito", carritoItems);
            }

            // Calcular totales
            double subtotal = calcularSubtotal(carritoItems);
            double envio = 3000.0; // Costo fijo de envío
            double total = subtotal + envio;

            model.addAttribute("carritoItems", carritoItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("envio", envio);
            model.addAttribute("total", total);
            model.addAttribute("cantidadItems", carritoItems.size());

            return "carrito/carrito";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el carrito: " + e.getMessage());
            return "carrito/carrito";
        }
    }

    // Agregar producto al carrito
    @PostMapping("/carrito/agregar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(
            @RequestParam Long productoId,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (productoId == null || productoId <= 0) {
                response.put("success", false);
                response.put("message", "ID de producto inválido");
                return ResponseEntity.badRequest().body(response);
            }

            if (cantidad <= 0) {
                response.put("success", false);
                response.put("message", "La cantidad debe ser mayor a cero");
                return ResponseEntity.badRequest().body(response);
            }

            // Obtener producto por ID 
            Producto producto = productoService.getProductoPorId(productoId);
            if (producto == null) {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar stock disponible
            if (producto.getStockActual() < cantidad) {
                response.put("success", false);
                response.put("message", "Stock insuficiente. Disponible: " + producto.getStockActual());
                return ResponseEntity.badRequest().body(response);
            }

            // Obtener carrito de la sesión
            @SuppressWarnings("unchecked")
            List<Carrito> carritoItems = (List<Carrito>) session.getAttribute("carrito");

            if (carritoItems == null) {
                carritoItems = new ArrayList<>();
            }

            // Verificar si el producto ya está en el carrito
            boolean productoExistente = false;
            for (Carrito item : carritoItems) {
                if (item.getInventarioId() != null
                        && item.getInventarioId().equals(producto.getInventarioId())) {
                    // Actualizar cantidad
                    int nuevaCantidad = item.getCantidad() + cantidad;
                    if (nuevaCantidad > producto.getStockActual()) {
                        response.put("success", false);
                        response.put("message", "No se puede agregar más cantidad. Stock disponible: "
                                + producto.getStockActual());
                        return ResponseEntity.badRequest().body(response);
                    }
                    item.setCantidad(nuevaCantidad);
                    item.setSubtotalDetalle(nuevaCantidad * producto.getPrecio());
                    productoExistente = true;
                    break;
                }
            }

            // Si no existe, crear nuevo item
            if (!productoExistente) {
                Carrito nuevoItem = new Carrito();
                nuevoItem.setInventarioId(producto.getInventarioId());
                nuevoItem.setCantidad(cantidad);
                nuevoItem.setPrecioUnitario(producto.getPrecio());
                nuevoItem.setSubtotalDetalle(cantidad * producto.getPrecio());
                nuevoItem.setDescuentoItem(0.0);

                // AGREGAR información completa del producto
                nuevoItem.setNombreProducto(producto.getNombre());
                nuevoItem.setImagenProducto(producto.getRutaImagen());

                carritoItems.add(nuevoItem);
            }

            // Actualizar sesión
            session.setAttribute("carrito", carritoItems);

            response.put("success", true);
            response.put("message", "Producto agregado al carrito");
            response.put("cantidadItems", carritoItems.size());
            response.put("total", calcularSubtotal(carritoItems));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error detallado al agregar producto: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Error al agregar producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Actualizar cantidad en el carrito
    @PostMapping("/carrito/actualizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarCantidad(
            @RequestParam Long inventarioId,
            @RequestParam int cantidad,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (cantidad <= 0) {
                response.put("success", false);
                response.put("message", "La cantidad debe ser mayor a cero");
                return ResponseEntity.badRequest().body(response);
            }

            @SuppressWarnings("unchecked")
            List<Carrito> carritoItems = (List<Carrito>) session.getAttribute("carrito");

            if (carritoItems == null) {
                response.put("success", false);
                response.put("message", "Carrito vacío");
                return ResponseEntity.badRequest().body(response);
            }

            boolean itemEncontrado = false;
            for (Carrito item : carritoItems) {
                if (item.getInventarioId() != null
                        && item.getInventarioId().equals(inventarioId)) {
                    item.setCantidad(cantidad);
                    item.setSubtotalDetalle(cantidad * item.getPrecioUnitario());
                    itemEncontrado = true;
                    break;
                }
            }

            if (!itemEncontrado) {
                response.put("success", false);
                response.put("message", "Producto no encontrado en el carrito");
                return ResponseEntity.badRequest().body(response);
            }

            session.setAttribute("carrito", carritoItems);

            response.put("success", true);
            response.put("message", "Cantidad actualizada");
            response.put("total", calcularSubtotal(carritoItems));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar cantidad: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Eliminar producto del carrito
    @PostMapping("/carrito/eliminar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarDelCarrito(
            @RequestParam Long inventarioId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            @SuppressWarnings("unchecked")
            List<Carrito> carritoItems = (List<Carrito>) session.getAttribute("carrito");

            if (carritoItems == null) {
                response.put("success", false);
                response.put("message", "Carrito vacío");
                return ResponseEntity.badRequest().body(response);
            }

            boolean itemEliminado = carritoItems.removeIf(item
                    -> item.getInventarioId() != null && item.getInventarioId().equals(inventarioId));

            if (!itemEliminado) {
                response.put("success", false);
                response.put("message", "Producto no encontrado en el carrito");
                return ResponseEntity.badRequest().body(response);
            }

            session.setAttribute("carrito", carritoItems);

            response.put("success", true);
            response.put("message", "Producto eliminado del carrito");
            response.put("cantidadItems", carritoItems.size());
            response.put("total", calcularSubtotal(carritoItems));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Limpiar carrito
    @PostMapping("/carrito/limpiar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> limpiarCarrito(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            session.removeAttribute("carrito");

            response.put("success", true);
            response.put("message", "Carrito limpiado");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al limpiar carrito: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Mostrar página de checkout
    @GetMapping("/checkout")
    public String mostrarCheckout(HttpSession session, Model model) {
        try {
            @SuppressWarnings("unchecked")
            List<Carrito> carritoItems = (List<Carrito>) session.getAttribute("carrito");

            if (carritoItems == null || carritoItems.isEmpty()) {
                return "redirect:/carrito?error=carrito_vacio";
            }

            // Calcular totales
            double subtotal = calcularSubtotal(carritoItems);
            double envio = 3000.0;
            double impuesto = subtotal * 0.13; // 13% de IVA
            double total = subtotal + envio + impuesto;

            model.addAttribute("carritoItems", carritoItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("envio", envio);
            model.addAttribute("impuesto", impuesto);
            model.addAttribute("total", total);

            return "carrito/checkout";

        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar checkout: " + e.getMessage());
            return "redirect:/carrito/carrito";
        }
    }

    // Procesar la compra
    @PostMapping("/checkout/procesar")
    public String procesarCompra(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String direccion,
            @RequestParam String telefono,
            @RequestParam String ciudad,
            @RequestParam String provincia,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String numeroTarjeta,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            @SuppressWarnings("unchecked")
            List<Carrito> carritoItems = (List<Carrito>) session.getAttribute("carrito");

            if (carritoItems == null || carritoItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El carrito está vacío");
                return "redirect:/carrito/carrito";
            }

            // Crear venta principal
            Carrito ventaHeader = new Carrito();
            ventaHeader.setClienteId(1L); // Por ahora usar cliente por defecto
            ventaHeader.setCodigoVenta(generarCodigoVenta());

            // Calcular totales
            double subtotal = calcularSubtotal(carritoItems);
            double envio = 3000.0;
            double impuesto = subtotal * 0.13;
            double total = subtotal + envio + impuesto;

            ventaHeader.setSubtotal(subtotal);
            ventaHeader.setImpuesto(impuesto);
            ventaHeader.setDescuento(0.0);
            ventaHeader.setTotal(total);
            ventaHeader.setMedioPago(metodoPago);
            ventaHeader.setEstado("Pendiente");

            // Procesar la venta
            Long ventaId = ventaService.procesarVenta(ventaHeader, carritoItems);

            if (ventaId != null) {
                // Limpiar carrito después de la venta exitosa
                session.removeAttribute("carrito");

                redirectAttributes.addFlashAttribute("success",
                        "Compra procesada exitosamente. Número de venta: " + ventaId);
                redirectAttributes.addAttribute("ventaId", ventaId);

                return "redirect:/venta/confirmacion";
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al procesar la compra");
                return "redirect:/carrito/checkout";
            }

        } catch (Exception e) {
            System.err.println("Error al procesar compra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al procesar la compra: " + e.getMessage());
            return "redirect:/carrito/checkout";
        }
    }

    // Mostrar confirmación de venta
    @GetMapping("/venta/confirmacion")
    public String mostrarConfirmacion(
            @RequestParam Long ventaId,
            Model model) {

        try {
            Carrito venta = ventaService.getVentaById(ventaId);
            List<Carrito> detalles = ventaService.getDetallesVenta(ventaId);

            model.addAttribute("venta", venta);
            model.addAttribute("detalles", detalles);

            return "carrito/confirmacion";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar confirmación: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Ver historial de compras
    @GetMapping("/historialCompras")
    public String verHistorialCompras(@RequestParam(required = false) Long clienteId, Model model) {
        try {
            List<Carrito> historial;

            if (clienteId != null) {
                historial = ventaService.getVentasByCliente(clienteId);
            } else {
                // Por defecto mostrar todas las ventas (o del cliente logueado)
                historial = ventaService.getVentas();
            }

            model.addAttribute("historial", historial);
            return "carrito/historialCompras";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar historial: " + e.getMessage());
            return "carrito/historialCompras";
        }
    }

    // Obtener información del carrito (AJAX)
    @GetMapping("/carrito/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerInfoCarrito(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            @SuppressWarnings("unchecked")
            List<Carrito> carritoItems = (List<Carrito>) session.getAttribute("carrito");

            if (carritoItems == null) {
                carritoItems = new ArrayList<>();
            }

            response.put("cantidadItems", carritoItems.size());
            response.put("total", calcularSubtotal(carritoItems));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al obtener información del carrito");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Métodos auxiliares privados
    private double calcularSubtotal(List<Carrito> carritoItems) {
        return carritoItems.stream()
                .mapToDouble(Carrito::getSubtotalDetalle)
                .sum();
    }

    private String generarCodigoVenta() {
        // Generar código único basado en timestamp
        return "V" + System.currentTimeMillis();
    }
}