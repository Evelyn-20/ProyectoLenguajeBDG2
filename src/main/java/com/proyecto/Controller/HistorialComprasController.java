package com.proyecto.Controller;

import com.proyecto.Domain.Carrito;
import com.proyecto.Service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/historialCompras")
public class HistorialComprasController {

    @Autowired
    private VentaService ventaService;

    // Mostrar historial de compras del cliente logueado
    @GetMapping("/listado")
    public String verHistorialCompras(Model model, RedirectAttributes redirectAttributes) {
        try {
            // Obtener el ID del cliente logueado
            Long clienteId = obtenerClienteIdLogueado();

            if (clienteId == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo identificar al cliente. Por favor, inicia sesión nuevamente.");
                return "redirect:/login";
            }

            System.out.println("Cargando historial para cliente ID: " + clienteId);

            // Obtener historial de compras del cliente
            List<Carrito> historialCompras = ventaService.getHistorialComprasPorCliente(clienteId);

            System.out.println("Compras encontradas: " + historialCompras.size());

            model.addAttribute("compras", historialCompras);
            model.addAttribute("clienteId", clienteId);

            // Agregar información adicional si no hay compras
            if (historialCompras.isEmpty()) {
                model.addAttribute("mensajeVacio", "Aún no has realizado ninguna compra. ¡Explora nuestros productos!");
            }

            return "carrito/historialCompras";

        } catch (Exception e) {
            System.err.println("Error al cargar historial de compras: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el historial de compras: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Ver detalle de una compra específica
    @GetMapping("/detalle/{ventaId}")
    public String verDetalleCompra(@PathVariable Long ventaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Obtener el ID del cliente logueado
            Long clienteId = obtenerClienteIdLogueado();

            if (clienteId == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo identificar al cliente. Por favor, inicia sesión nuevamente.");
                return "redirect:/login";
            }

            System.out.println("Cliente " + clienteId + " solicita detalle de venta: " + ventaId);

            // Obtener la venta
            Carrito venta = ventaService.getVentaById(ventaId);

            if (venta == null) {
                redirectAttributes.addFlashAttribute("error", "Compra no encontrada");
                return "redirect:/carrito/historialCompras";
            }

            // Verificar que la venta pertenece al cliente logueado
            if (!venta.getClienteId().equals(clienteId)) {
                System.err.println("Cliente " + clienteId + " intentó acceder a venta " + ventaId + " que pertenece a cliente " + venta.getClienteId());
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para ver esta compra");
                return "redirect:/carrito/historialCompras";
            }

            // Obtener detalles de la compra
            List<Carrito> detalles = ventaService.getDetallesVentaConProductos(ventaId);

            System.out.println("Detalles de productos encontrados: " + detalles.size());

            model.addAttribute("venta", venta);
            model.addAttribute("detalles", detalles);
            model.addAttribute("puedeSerCancelada", puedeSerCancelada(venta.getEstado()));

            return "carrito/detalleCompraCliente";

        } catch (Exception e) {
            System.err.println("Error al cargar detalle de compra: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el detalle de la compra: " + e.getMessage());
            return "redirect:/carrito/historialCompras";
        }
    }

    // Cancelar compra (solo si no está completada o cancelada)
    @PostMapping("/cancelar/{ventaId}")
    public String cancelarCompra(@PathVariable Long ventaId, RedirectAttributes redirectAttributes) {
        try {
            // Obtener el ID del cliente logueado
            Long clienteId = obtenerClienteIdLogueado();

            if (clienteId == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo identificar al cliente. Por favor, inicia sesión nuevamente.");
                return "redirect:/login";
            }

            // Validar que la compra puede ser cancelada por este cliente
            if (!ventaService.puedeClienteCancelarCompra(ventaId, clienteId)) {
                redirectAttributes.addFlashAttribute("error", "No se puede cancelar esta compra");
                return "redirect:/carrito/historialCompras";
            }

            // Cancelar la compra
            ventaService.cambiarEstadoVentaSeguro(ventaId, "Cancelada");

            System.out.println("Cliente " + clienteId + " canceló exitosamente la compra " + ventaId);

            redirectAttributes.addFlashAttribute("success", "Compra cancelada exitosamente");
            return "redirect:/carrito/historialCompras";

        } catch (Exception e) {
            System.err.println("Error al cancelar compra: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la compra: " + e.getMessage());
            return "redirect:/carrito/detalleCompraCliente/" + ventaId;
        }
    }

    // API para cancelar compra (AJAX)
    @PostMapping("/api/cancelar/{ventaId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelarCompraAPI(@PathVariable Long ventaId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Obtener el ID del cliente logueado
            Long clienteId = obtenerClienteIdLogueado();

            if (clienteId == null) {
                response.put("success", false);
                response.put("message", "No se pudo identificar al cliente");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            System.out.println("API: Cliente " + clienteId + " solicita cancelar venta " + ventaId);

            // Validar que la compra puede ser cancelada por este cliente
            if (!ventaService.puedeClienteCancelarCompra(ventaId, clienteId)) {
                response.put("success", false);
                response.put("message", "No se puede cancelar esta compra");
                return ResponseEntity.badRequest().body(response);
            }

            // Cancelar la compra
            ventaService.cambiarEstadoVentaSeguro(ventaId, "Cancelada");

            System.out.println("API: Cliente " + clienteId + " canceló exitosamente la compra " + ventaId);

            response.put("success", true);
            response.put("message", "Compra cancelada exitosamente");
            response.put("ventaId", ventaId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error en API cancelar compra: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al cancelar la compra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener estadísticas del cliente (opcional)
    @GetMapping("/estadisticas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasCliente() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Obtener el ID del cliente logueado
            Long clienteId = obtenerClienteIdLogueado();

            if (clienteId == null) {
                response.put("success", false);
                response.put("message", "Cliente no identificado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Obtener compras del cliente
            List<Carrito> compras = ventaService.getHistorialComprasPorCliente(clienteId);

            // Calcular estadísticas básicas
            int totalCompras = compras.size();
            int comprasCompletadas = (int) compras.stream().filter(c -> "Completada".equals(c.getEstado())).count();
            int comprasPendientes = (int) compras.stream().filter(c -> "Pendiente".equals(c.getEstado())).count();
            int comprasCanceladas = (int) compras.stream().filter(c -> "Cancelada".equals(c.getEstado())).count();

            double totalGastado = compras.stream()
                    .filter(c -> "Completada".equals(c.getEstado()))
                    .mapToDouble(Carrito::getTotal)
                    .sum();

            response.put("success", true);
            response.put("clienteId", clienteId);
            response.put("totalCompras", totalCompras);
            response.put("comprasCompletadas", comprasCompletadas);
            response.put("comprasPendientes", comprasPendientes);
            response.put("comprasCanceladas", comprasCanceladas);
            response.put("totalGastado", totalGastado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error al obtener estadísticas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Método auxiliar para obtener el ID del cliente logueado
    private Long obtenerClienteIdLogueado() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                System.err.println("No hay autenticación válida");
                return null;
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername(); // Esto será el EMAIL

                System.out.println("Usuario autenticado: " + username);

                // El username es el EMAIL del cliente, buscar directamente por email
                Long clienteId = ventaService.getClienteIdPorUsername(username);
                System.out.println("Cliente ID obtenido: " + clienteId);
                return clienteId;

            } else if (principal instanceof String) {
                // En caso de que el principal sea directamente el email
                String username = (String) principal;
                return ventaService.getClienteIdPorUsername(username);
            }

            System.err.println("Principal no es UserDetails: " + principal.getClass().getName());
            return null;

        } catch (Exception e) {
            System.err.println("Error al obtener cliente ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Método auxiliar para determinar si una compra puede ser cancelada
    private boolean puedeSerCancelada(String estado) {
        if (estado == null) {
            return false;
        }

        // Solo se pueden cancelar las compras que no estén completadas o ya canceladas
        return !estado.equals("Completada") && !estado.equals("Cancelada");
    }

    // Método auxiliar para validar permisos del cliente
    private boolean validarPermisosCliente(Long ventaId, Long clienteId) {
        try {
            return ventaService.compraPerteneceACliente(ventaId, clienteId);
        } catch (Exception e) {
            System.err.println("Error al validar permisos: " + e.getMessage());
            return false;
        }
    }
}
