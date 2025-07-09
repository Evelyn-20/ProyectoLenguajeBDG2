package com.proyecto.Controller;

import com.proyecto.Domain.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ProductoController {
    
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
    
    @GetMapping("/producto/fragmentos")
    public String fragmentos(Model model) {
        return "producto/fragmentos";
    }
    
    @GetMapping("/inventario/listado")
    public String verListado(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto/listado";
    }

    @GetMapping("/producto/modificar")
    public String modificar(Model model) {
        return "producto/modificar";
    }
    
    @GetMapping("/producto/agregar")
    public String agregar(Model model) {
        return "producto/AgregarProducto";
    }
}