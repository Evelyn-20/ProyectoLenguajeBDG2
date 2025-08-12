package com.proyecto.Controller;

import com.proyecto.Domain.Categoria;
import com.proyecto.Domain.Producto;
import com.proyecto.Service.CategoriaService;
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
    
    @Autowired
    private CategoriaService categoriaService;
    
    // Páginas principales de categorías con subcategorías dinámicas
    @GetMapping("/mujer")
    public String mujer(Model model) {
        List<Categoria> categorias = categoriaService.getSubcategoriasMujer();
        model.addAttribute("categorias", categorias);
        return "producto/categoriaMujer";
    }
    
    @GetMapping("/hombre")
    public String hombre(Model model) {
        List<Categoria> categorias = categoriaService.getSubcategoriasHombre();
        model.addAttribute("categorias", categorias);
        return "producto/categoriaHombre";
    }
    
    @GetMapping("/zapatos")
    public String zapatos(Model model) {
        List<Categoria> categorias = categoriaService.getSubcategoriasZapatos();
        model.addAttribute("categorias", categorias);
        return "producto/categoriaZapatos";
    }
    
    @GetMapping("/accesorios")
    public String accesorios(Model model) {
        List<Categoria> categorias = categoriaService.getSubcategoriasAccesorios();
        model.addAttribute("categorias", categorias);
        return "producto/categoriaAccesorios";
    }
    
    // Páginas de productos por categoría específica - MUJER
    @GetMapping("/producto/vestidos")
    public String vestidos(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(8L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Vestidos");
        return "producto/vestidosMujer";
    }
    
    @GetMapping("/producto/blusas-camisetas")
    public String blusas(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(9L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Blusas y Camisetas");
        return "producto/blusasMujer";
    }
    
    @GetMapping("/producto/faldas")
    public String faldas(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(10L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Faldas");
        return "producto/enaguasMujer";
    }
    
    @GetMapping("/producto/pantalones-mujer")
    public String pantalonesMujer(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(11L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Pantalones para Mujer");
        return "producto/pantalonesMujer";
    }
    
    @GetMapping("/producto/chaquetas-sudaderas-mujer")
    public String chaquetasMujer(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(12L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Chaquetas y Sudaderas para Mujer");
        return "producto/chaquetasMujer";
    }
    
    // Páginas de productos por categoría específica - HOMBRE
    @GetMapping("/producto/camisas-camisetas")
    public String camisas(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(5L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Camisas y Camisetas");
        return "producto/camisasHombre";
    }
    
    @GetMapping("/producto/pantalones-hombre")
    public String pantalonesHombre(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(6L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Pantalones para Hombre");
        return "producto/pantalonesHombre";
    }
    
    @GetMapping("/producto/chaquetas-sudaderas-hombre")
    public String chaquetasHombre(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(7L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Chaquetas y Sudaderas para Hombre");
        return "producto/chaquetasHombre";
    }
    
    // Páginas de productos por categoría específica - ZAPATOS
    @GetMapping("/producto/tenis")
    public String tenis(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(13L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Tennis");
        return "producto/tennis";
    }
    
    @GetMapping("/producto/botas")
    public String botas(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(14L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Botas");
        return "producto/botas";
    }
    
    @GetMapping("/producto/zapatos-formales")
    public String zapatosFormales(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(15L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Zapatos Formales");
        return "producto/zapatosFormales";
    }
    
    @GetMapping("/producto/sandalias")
    public String sandalias(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(16L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Sandalias");
        return "producto/sandalias";
    }
    
    @GetMapping("/producto/zapatos-casuales")
    public String zapatosCasuales(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(17L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Zapatos Casuales");
        return "producto/zapatosCasuales";
    }
    
    // Páginas de productos por categoría específica - ACCESORIOS
    @GetMapping("/producto/bolso-mochila")
    public String bolsos(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(18L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Bolsos y Mochilas");
        return "producto/bolso";
    }
    
    @GetMapping("/producto/joyería")
    public String joyeria(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(19L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Joyería");
        return "producto/joyas";
    }
    
    @GetMapping("/producto/gorras-sombreros")
    public String gorras(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(20L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Gorras y Sombreros");
        return "producto/gorras";
    }
    
    @GetMapping("/producto/cinturones")
    public String cinturones(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(21L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Cinturones");
        return "producto/fajas";
    }
    
    @GetMapping("/producto/lentes")
    public String lentes(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(22L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Lentes");
        return "producto/lentes";
    }
    
    @GetMapping("/producto/bufandas-guantes")
    public String bufandas(Model model) {
        List<Producto> productos = productoService.getProductosPorCategoria(23L);
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", "Bufandas y Guantes");
        return "producto/bufandas";
    }
    
    // CRUD y administración de productos
    @GetMapping("/inventario/listado")
    public String verListado(Model model) {
        List<Producto> productos = productoService.getProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("producto", new Producto());
        return "producto/listado";
    }

    @GetMapping("/producto/buscar")
    public String buscarProductos(@RequestParam(required = false) String busqueda, Model model) {
        List<Producto> productos;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            productos = productoService.buscarProductos(busqueda);
        } else {
            productos = productoService.getProductos();
        }
        model.addAttribute("productos", productos);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("producto", new Producto());
        return "producto/listado";
    }

    @GetMapping("/producto/agregar")
    public String agregar(Model model) {
        List<Categoria> categorias = categoriaService.getCategorias();
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categorias);
        return "producto/AgregarProducto";
    }

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

    @GetMapping("/producto/modificar/{id}")
    public String modificar(@PathVariable Long id, Model model) {
        Producto producto = productoService.getProductoPorId(id);
        if (producto == null) {
            model.addAttribute("error", "Producto no encontrado");
            return "redirect:/inventario/listado";
        }
        List<Categoria> categorias = categoriaService.getCategorias();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categorias);
        return "producto/modificar";
    }

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

    @GetMapping("/producto/activos")
    public String productosActivos(Model model) {
        List<Producto> productos = productoService.getProductosActivos();
        model.addAttribute("productos", productos);
        return "producto/listado";
    }
}