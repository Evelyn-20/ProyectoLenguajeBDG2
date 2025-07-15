package com.proyecto.Controller;

import com.proyecto.Domain.Producto;
import com.proyecto.Service.ProductoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @GetMapping("/mujer")
    public String mujer(Model model) {
        return "producto/categoriaMujer";
    }
    
    @GetMapping("/producto/vestidos")
    public String vestidos(Model model) {
        return "producto/vestidosMujer";
    }
    
    @GetMapping("/producto/blusas-camisetas")
    public String blusas(Model model) {
        return "producto/blusasMujer";
    }
    
    @GetMapping("/producto/faldas")
    public String enaguas(Model model) {
        return "producto/enaguasMujer";
    }
    
    @GetMapping("/producto/pantalones-mujer")
    public String pantalonesMujer(Model model) {
        return "producto/pantalonesMujer";
    }
    
    @GetMapping("/producto/chaquetas-sudaderas-mujer")
    public String chaquetasMujer(Model model) {
        return "producto/chaquetasMujer";
    }
    
    @GetMapping("/hombre")
    public String hombre(Model model) {
        return "producto/categoriaHombre";
    }
    
    @GetMapping("/producto/camisas-camisetas")
    public String camisas(Model model) {
        return "producto/camisasHombre";
    }
    
    @GetMapping("/producto/chaquetas-sudaderas-hombre")
    public String chaquetasHombre(Model model) {
        return "producto/chaquetasHombre";
    }
    
    @GetMapping("/producto/pantalones-hombre")
    public String pantalones(Model model) {
        return "producto/pantalonesHombre";
    }
    
    @GetMapping("/zapatos")
    public String zapatos(Model model) {
        return "producto/categoriaZapatos";
    }
    
    @GetMapping("/producto/tenis")
    public String tenis(Model model) {
        return "producto/tennis";
    }
    
    @GetMapping("/producto/botas")
    public String botas(Model model) {
        return "producto/botas";
    }
    
    @GetMapping("/producto/zapatos-formales")
    public String zapatosFormales(Model model) {
        return "producto/zapatosFormales";
    }
    
    @GetMapping("/producto/zapatos-casuales")
    public String zapatosCasuales(Model model) {
        return "producto/zapatosCasuales";
    }
    
    @GetMapping("/producto/sandalias")
    public String sandalias(Model model) {
        return "producto/sandalias";
    }
    
    @GetMapping("/accesorios")
    public String accesorios(Model model) {
        return "producto/categoriaAccesorios";
    }
    
    @GetMapping("/producto/gorras-sombreros")
    public String gorras(Model model) {
        return "producto/gorras";
    }
    
    @GetMapping("/producto/lentes")
    public String lentes(Model model) {
        return "producto/lentes";
    }
    
    @GetMapping("/producto/bolso-mochila")
    public String bolso(Model model) {
        return "producto/bolso";
    }
    
    @GetMapping("/producto/bufandas-guantes")
    public String bufandas(Model model) {
        return "producto/bufandas";
    }
    
    @GetMapping("/producto/cinturones")
    public String fajas(Model model) {
        return "producto/fajas";
    }
    
    @GetMapping("/producto/joyería")
    public String joyas(Model model) {
        return "producto/joyas";
    }
    
    // Listado de productos con búsqueda
    @GetMapping("/inventario/listado")
    public String verListado(Model model) {
        List<Producto> productos = productoService.getProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("producto", new Producto());
        return "producto/listado";
    }

    // Búsqueda de productos
    @GetMapping("/producto/buscar")
    public String buscarProductos(@RequestParam(required = false) String busqueda, Model model) {
        List<Producto> productos = productoService.buscarProductos(busqueda);
        model.addAttribute("productos", productos);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("producto", new Producto());
        return "producto/listado";
    }

    // Mostrar formulario para agregar producto
    @GetMapping("/producto/agregar")
    public String agregar(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto/AgregarProducto";
    }

    // Procesar formulario de agregar producto
    @PostMapping("/producto/agregar")
    public String agregarProducto(Producto producto, RedirectAttributes redirectAttributes) {
        try {
            productoService.registrarProducto(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al agregar producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/inventario/listado";
    }

    // Mostrar formulario para modificar producto
    @GetMapping("/producto/modificar/{id}")
    public String modificar(@PathVariable Long id, Model model) {
        Producto producto = productoService.getProductoPorId(id);
        if (producto == null) {
            model.addAttribute("error", "Producto no encontrado");
            return "redirect:/inventario/listado";
        }
        model.addAttribute("producto", producto);
        return "producto/modificar";
    }

    // Procesar formulario de modificar producto
    @PostMapping("/producto/modificar")
    public String modificarProducto(Producto producto, RedirectAttributes redirectAttributes) {
        try {
            productoService.actualizarProducto(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/inventario/listado";
    }

    // Eliminar producto (deshabilitar)
    @GetMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/inventario/listado";
    }

    // Consultar producto por código
    @GetMapping("/producto/consultar/{codigo}")
    public String consultarProducto(@PathVariable String codigo, Model model) {
        Producto producto = productoService.consultarProductoPorCodigo(codigo);
        if (producto == null) {
            model.addAttribute("error", "Producto no encontrado");
        } else {
            model.addAttribute("producto", producto);
        }
        return "producto/detalle";
    }

    // Endpoint específico para obtener solo productos activos
    @GetMapping("/producto/activos")
    public String productosActivos(Model model) {
        List<Producto> productos = productoService.getProductosActivos();
        model.addAttribute("productos", productos);
        return "producto/listado";
    }
}