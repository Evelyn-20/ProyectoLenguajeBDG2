package com.proyecto.Controller;

import com.proyecto.Domain.Carrito;
import com.proyecto.Service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@Controller
@RequestMapping("/ventas")
public class VentasController {

    @Autowired
    private VentaService ventaService;

    // Mostrar detalles de una venta específica
    @GetMapping("/detalle/{ventaId}")
    public String verDetalleVenta(@PathVariable Long ventaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== INICIO verDetalleVenta ===");
            System.out.println("VentaId recibido: " + ventaId);

            // Validar que el ID no sea nulo
            if (ventaId == null || ventaId <= 0) {
                System.err.println("ID de venta inválido: " + ventaId);
                redirectAttributes.addFlashAttribute("error", "ID de venta no válido");
                return "redirect:/ventas/listado";
            }

            // Buscar la venta
            Carrito venta = ventaService.getVentaById(ventaId);
            System.out.println("Resultado de ventaService.getVentaById: " + (venta != null ? "Encontrada" : "NULL"));

            if (venta == null) {
                System.err.println("Venta no encontrada con ID: " + ventaId);
                redirectAttributes.addFlashAttribute("error", "Venta no encontrada con ID: " + ventaId);
                return "redirect:/ventas/listado";
            }

            System.out.println("Venta encontrada - ID: " + venta.getVentaId() + ", Estado: " + venta.getEstado());

            // Obtener detalles
            List<Carrito> detalles = ventaService.getDetallesVentaConProductos(ventaId);
            System.out.println("Detalles encontrados: " + (detalles != null ? detalles.size() : "NULL"));

            model.addAttribute("venta", venta);
            model.addAttribute("detalles", detalles);

            // Agregar información sobre si la venta puede ser modificada
            boolean puedeModificar = ventaService.puedeModificarVenta(ventaId);
            model.addAttribute("puedeModificar", puedeModificar);
            System.out.println("Puede modificar: " + puedeModificar);

            // Agregar estados válidos para transición
            List<String> estadosValidos = ventaService.getEstadosValidosParaTransicion(venta.getEstado());
            model.addAttribute("estadosValidos", estadosValidos);
            System.out.println("Estados válidos: " + estadosValidos);

            System.out.println("=== FIN verDetalleVenta (exitoso) ===");
            return "carrito/detalleVenta";

        } catch (Exception e) {
            System.err.println("=== ERROR en verDetalleVenta ===");
            System.err.println("VentaId: " + ventaId);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("error", "Error al cargar los detalles de la venta: " + e.getMessage());
            return "redirect:/ventas/listado";
        }
    }

    // Mostrar formulario para cambiar estado de una venta
    @GetMapping("/cambiar-estado/{ventaId}")
    public String mostrarFormularioCambioEstado(@PathVariable Long ventaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== INICIO mostrarFormularioCambioEstado ===");
            System.out.println("VentaId recibido: " + ventaId);

            // Validar que el ID no sea nulo
            if (ventaId == null || ventaId <= 0) {
                System.err.println("ID de venta inválido: " + ventaId);
                redirectAttributes.addFlashAttribute("error", "ID de venta no válido");
                return "redirect:/ventas/listado";
            }

            Carrito venta = ventaService.getVentaById(ventaId);
            System.out.println("Resultado de ventaService.getVentaById: " + (venta != null ? "Encontrada" : "NULL"));

            if (venta == null) {
                System.err.println("Venta no encontrada con ID: " + ventaId);
                redirectAttributes.addFlashAttribute("error", "Venta no encontrada con ID: " + ventaId);
                return "redirect:/ventas/listado";
            }

            System.out.println("Venta encontrada - ID: " + venta.getVentaId() + ", Estado: " + venta.getEstado());

            // Verificar si la venta puede ser modificada
            if (!ventaService.puedeModificarVenta(ventaId)) {
                redirectAttributes.addFlashAttribute("error",
                        "Esta venta no puede ser modificada (estado: " + venta.getEstado() + ")");
                return "redirect:/ventas/detalle/" + ventaId;
            }

            // Obtener estados válidos para la transición
            List<String> estadosValidos = ventaService.getEstadosValidosParaTransicion(venta.getEstado());
            System.out.println("Estados válidos: " + estadosValidos);

            if (estadosValidos.isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "No hay estados válidos para cambiar desde el estado actual: " + venta.getEstado());
                return "redirect:/ventas/detalle/" + ventaId;
            }

            model.addAttribute("venta", venta);
            model.addAttribute("estados", estadosValidos);

            System.out.println("=== FIN mostrarFormularioCambioEstado (exitoso) ===");
            return "carrito/cambiarEstadoVenta";

        } catch (Exception e) {
            System.err.println("=== ERROR en mostrarFormularioCambioEstado ===");
            System.err.println("VentaId: " + ventaId);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("error", "Error al cargar la venta: " + e.getMessage());
            return "redirect:/ventas/listado";
        }
    }

    // Mostrar listado de todas las ventas
    @GetMapping("/listado")
    public String verListadoVentas(
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            Model model) {
        try {
            System.out.println("=== INICIO verListadoVentas ===");
            System.out.println("Estado: " + estado + ", Búsqueda: " + busqueda);

            List<Carrito> ventas;

            // Usar el nuevo método de búsqueda
            if ((busqueda != null && !busqueda.trim().isEmpty())
                    || (estado != null && !estado.isEmpty() && !estado.equals("TODOS"))) {
                System.out.println("Realizando búsqueda con filtros");
                ventas = ventaService.buscarVentas(busqueda, estado);
            } else {
                System.out.println("Obteniendo todas las ventas");
                ventas = ventaService.getVentas();
            }

            System.out.println("Ventas encontradas: " + (ventas != null ? ventas.size() : "NULL"));

            if (ventas != null && !ventas.isEmpty()) {
                System.out.println("Primera venta - ID: " + ventas.get(0).getVentaId()
                        + ", Código: " + ventas.get(0).getCodigoVenta());
            }

            model.addAttribute("ventas", ventas);
            model.addAttribute("busqueda", busqueda);
            model.addAttribute("estadoSeleccionado", estado);

            // Lista de estados para el filtro
            model.addAttribute("estados", Arrays.asList("Pendiente", "Confirmada", "En Proceso", "Completada", "Cancelada"));

            System.out.println("=== FIN verListadoVentas (exitoso) ===");
            return "carrito/listadoVentas";

        } catch (Exception e) {
            System.err.println("=== ERROR en verListadoVentas ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("error", "Error al cargar listado de ventas: " + e.getMessage());
            model.addAttribute("ventas", List.of());
            model.addAttribute("estados", Arrays.asList("Pendiente", "Confirmada", "En Proceso", "Completada", "Cancelada"));
            return "carrito/listadoVentas";
        }
    }

    // Procesar cambio de estado (formulario)
    @PostMapping("/cambiar-estado")
    public String cambiarEstadoVenta(
            @RequestParam Long ventaId,
            @RequestParam String estado,
            @RequestParam(required = false) String observaciones,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar parámetros
            if (ventaId == null || ventaId <= 0) {
                redirectAttributes.addFlashAttribute("error", "ID de venta no válido");
                return "redirect:/ventas/listado";
            }

            if (estado == null || estado.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un estado");
                return "redirect:/ventas/cambiar-estado/" + ventaId;
            }

            // Verificar que la venta existe
            Carrito venta = ventaService.getVentaById(ventaId);
            if (venta == null) {
                redirectAttributes.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/ventas/listado";
            }

            // Verificar si la venta puede ser modificada
            if (!ventaService.puedeModificarVenta(ventaId)) {
                redirectAttributes.addFlashAttribute("error",
                        "Esta venta no puede ser modificada (estado actual: " + venta.getEstado() + ")");
                return "redirect:/ventas/detalle/" + ventaId;
            }

            // Verificar que el estado es válido para la transición
            List<String> estadosValidos = ventaService.getEstadosValidosParaTransicion(venta.getEstado());
            if (!estadosValidos.contains(estado.trim())) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede cambiar de '" + venta.getEstado() + "' a '" + estado.trim() + "'");
                return "redirect:/ventas/cambiar-estado/" + ventaId;
            }

            // Cambiar el estado
            ventaService.cambiarEstadoVentaSeguro(ventaId, estado.trim());

            redirectAttributes.addFlashAttribute("success",
                    "Estado de la venta actualizado exitosamente de '" + venta.getEstado()
                    + "' a '" + estado.trim() + "'");

            return "redirect:/ventas/detalle/" + ventaId; // Redirigir al detalle para ver los cambios

        } catch (Exception e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al cambiar estado de venta: " + e.getMessage());
            return "redirect:/ventas/cambiar-estado/" + ventaId;
        }
    }

    // API REST para cambio de estado (AJAX)
    @PostMapping("/api/cambiar-estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarEstadoVentaAPI(
            @RequestParam Long ventaId,
            @RequestParam String estado) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validaciones
            if (ventaId == null || ventaId <= 0) {
                response.put("success", false);
                response.put("message", "ID de venta no válido");
                return ResponseEntity.badRequest().body(response);
            }

            if (estado == null || estado.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Estado no puede estar vacío");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar que la venta existe
            Carrito venta = ventaService.getVentaById(ventaId);
            if (venta == null) {
                response.put("success", false);
                response.put("message", "Venta no encontrada");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar si la venta puede ser modificada
            if (!ventaService.puedeModificarVenta(ventaId)) {
                response.put("success", false);
                response.put("message", "Esta venta no puede ser modificada (estado: " + venta.getEstado() + ")");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar que el estado es válido para la transición
            List<String> estadosValidos = ventaService.getEstadosValidosParaTransicion(venta.getEstado());
            if (!estadosValidos.contains(estado.trim())) {
                response.put("success", false);
                response.put("message", "No se puede cambiar de '" + venta.getEstado() + "' a '" + estado.trim() + "'");
                return ResponseEntity.badRequest().body(response);
            }

            String estadoAnterior = venta.getEstado();
            ventaService.cambiarEstadoVentaSeguro(ventaId, estado.trim());

            response.put("success", true);
            response.put("message", "Estado actualizado de '" + estadoAnterior + "' a '" + estado.trim() + "'");
            response.put("estadoAnterior", estadoAnterior);
            response.put("nuevoEstado", estado.trim());
            response.put("ventaId", ventaId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error en API cambiar estado: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Método de cambio de estado rápido (para botones directos)
    @PostMapping("/cambio-rapido")
    public String cambioRapidoEstado(
            @RequestParam Long ventaId,
            @RequestParam String estado,
            @RequestParam(value = "origen", defaultValue = "detalle") String origen,
            RedirectAttributes redirectAttributes) {
        try {
            // Verificar que la venta existe
            Carrito venta = ventaService.getVentaById(ventaId);
            if (venta == null) {
                redirectAttributes.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/ventas/listado";
            }

            // Verificar si la venta puede ser modificada
            if (!ventaService.puedeModificarVenta(ventaId)) {
                redirectAttributes.addFlashAttribute("error",
                        "Esta venta no puede ser modificada (estado: " + venta.getEstado() + ")");
                return determinarRedireccion(origen, ventaId);
            }

            // Verificar que el estado es válido para la transición
            List<String> estadosValidos = ventaService.getEstadosValidosParaTransicion(venta.getEstado());
            if (!estadosValidos.contains(estado)) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede cambiar de '" + venta.getEstado() + "' a '" + estado + "'");
                return determinarRedireccion(origen, ventaId);
            }

            String estadoAnterior = venta.getEstado();
            ventaService.cambiarEstadoVentaSeguro(ventaId, estado);

            redirectAttributes.addFlashAttribute("success",
                    "Estado actualizado exitosamente de '" + estadoAnterior + "' a '" + estado + "'");

        } catch (Exception e) {
            System.err.println("Error en cambio rápido: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al cambiar estado: " + e.getMessage());
        }

        return determinarRedireccion(origen, ventaId);
    }

    // Método auxiliar para determinar redirección
    private String determinarRedireccion(String origen, Long ventaId) {
        switch (origen.toLowerCase()) {
            case "listado":
                return "redirect:/ventas/listado";
            case "detalle":
            default:
                return "redirect:/ventas/detalle/" + ventaId;
        }
    }

    // Confirmar pago de venta
    @PostMapping("/confirmar-pago/{ventaId}")
    public String confirmarPago(
            @PathVariable Long ventaId,
            @RequestParam(required = false) String medioPago,
            RedirectAttributes redirectAttributes) {
        try {
            ventaService.confirmarPago(ventaId, medioPago);
            redirectAttributes.addFlashAttribute("success", "Pago confirmado exitosamente");

        } catch (Exception e) {
            System.err.println("Error al confirmar pago: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al confirmar pago: " + e.getMessage());
        }

        return "redirect:/ventas/detalle/" + ventaId;
    }

    // Completar venta
    @PostMapping("/completar/{ventaId}")
    public String completarVenta(@PathVariable Long ventaId, RedirectAttributes redirectAttributes) {
        try {
            ventaService.completarVenta(ventaId);
            redirectAttributes.addFlashAttribute("success", "Venta completada exitosamente");

        } catch (Exception e) {
            System.err.println("Error al completar venta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al completar venta: " + e.getMessage());
        }

        return "redirect:/ventas/detalle/" + ventaId;
    }

    // Cancelar venta
    @PostMapping("/cancelar/{ventaId}")
    public String cancelarVenta(@PathVariable Long ventaId, RedirectAttributes redirectAttributes) {
        try {
            ventaService.cancelarVenta(ventaId);
            redirectAttributes.addFlashAttribute("success", "Venta cancelada exitosamente");

        } catch (Exception e) {
            System.err.println("Error al cancelar venta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al cancelar venta: " + e.getMessage());
        }

        return "redirect:/ventas/detalle/" + ventaId;
    }

    // Obtener estadísticas de ventas (para dashboard)
    @GetMapping("/estadisticas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> response = new HashMap<>();

        try {
            int totalVentas = ventaService.getTotalVentas();
            List<Carrito> ventasPendientes = ventaService.getVentasPendientes();
            List<Carrito> ventasConfirmadas = ventaService.getVentasConfirmadas();
            List<Carrito> ventasDelDia = ventaService.getVentasDelDia();

            response.put("totalVentas", totalVentas);
            response.put("ventasPendientes", ventasPendientes.size());
            response.put("ventasConfirmadas", ventasConfirmadas.size());
            response.put("ventasDelDia", ventasDelDia.size());
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            response.put("success", false);
            response.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // MÉTODO CORREGIDO: verDetalleVenta con mejor debugging
// MÉTODO CORREGIDO: mostrarFormularioCambioEstado con mejor debugging
}
