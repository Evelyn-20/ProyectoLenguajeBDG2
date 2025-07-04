package com.proyecto.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ProductoController {
    
    // ===== RUTAS PARA MUJERES =====
    @GetMapping("/mujer")
    public String mujer(Model model) {
        return "producto/categoriaMujer";
    }
    
    @GetMapping("/producto/mujeres")
    public String mujeres(Model model) {
        return "producto/categoriaMujer";
    }
    
    @GetMapping("/producto/categoriaMujer")
    public String categoriaMujer(Model model) {
        return "producto/categoriaMujer";
    }
    
    @GetMapping("/producto/vestidos")
    public String vestidos(Model model) {
        return "producto/vestidos";
    }
    
    @GetMapping("/producto/blusas")
    public String blusas(Model model) {
        return "producto/blusasMujer";
    }
    
    @GetMapping("/producto/enaguas")
    public String faldas(Model model) {
        return "producto/enaguasMujer";
    }
    
    // ===== RUTAS PARA HOMBRES =====
    @GetMapping("/hombre")
    public String hombre(Model model) {
        return "producto/categoriaHombre";
    }
    
    @GetMapping("/producto/hombres")
    public String hombres(Model model) {
        return "producto/categoriaHombre";
    }
    
    @GetMapping("/producto/categoriaHombre")
    public String categoriaHombre(Model model) {
        return "producto/categoriaHombre";
    }
    
    @GetMapping("/producto/camisas")
    public String camisas(Model model) {
        return "producto/camisasHombre";
    }
    
    @GetMapping("/producto/chaquetas")
    public String chaquetas(Model model) {
        return "producto/chaquetasHombre";
    }
    
    @GetMapping("/producto/pantalones")
    public String pantalones(Model model) {
        return "producto/pantalonesHombre";
    }
    
    // ===== RUTAS PARA ZAPATOS =====
    @GetMapping("/zapatos")
    public String zapatos(Model model) {
        return "producto/categoriaZapatos";
    }
    
    @GetMapping("/producto/zapatos")
    public String productosZapatos(Model model) {
        return "producto/categoriaZapatos";
    }
    
    @GetMapping("/producto/categoriaZapatos")
    public String categoriaZapatos(Model model) {
        return "producto/categoriaZapatos";
    }
    
    @GetMapping("/producto/sneakers")
    public String sneakers(Model model) {
        return "producto/tennis";
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
    
    @GetMapping("/producto/sandalias")
    public String sandalias(Model model) {
        return "producto/fajas";
    }
    
    @GetMapping("/producto/fajas")
    public String fajas(Model model) {
        return "producto/fajas";
    }
    
    @GetMapping("/producto/mocasines")
    public String mocasines(Model model) {
        return "producto/mocasines";
    }
    
    @GetMapping("/producto/zapatos-deportivos")
    public String zapatosDeportivos(Model model) {
        return "producto/zapatosDeportivos";
    }
    
    @GetMapping("/producto/zapatos-casuales")
    public String zapatosCasuales(Model model) {
        return "producto/bolso";
    }
    
    @GetMapping("/producto/bolso")
    public String bolso(Model model) {
        return "producto/bolso";
    }
    
    // ===== RUTAS PARA ACCESORIOS =====
    @GetMapping("/accesorios")
    public String accesorios(Model model) {
        return "producto/categoriaAccesorios";
    }
    
    @GetMapping("/producto/accesorios")
    public String productosAccesorios(Model model) {
        return "producto/categoriaAccesorios";
    }
    
    @GetMapping("/producto/categoriaAccesorios")
    public String categoriaAccesorios(Model model) {
        return "producto/categoriaAccesorios";
    }
    
    @GetMapping("/producto/reloj")
    public String reloj(Model model) {
        return "producto/reloj";
    }
    
    @GetMapping("/producto/gorras")
    public String gorras(Model model) {
        return "producto/gorras";
    }
    
    @GetMapping("/producto/lentes")
    public String lentes(Model model) {
        return "producto/lentes";
    }
}