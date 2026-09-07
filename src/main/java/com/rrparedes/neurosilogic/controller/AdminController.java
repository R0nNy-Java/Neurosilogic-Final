package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.service.MedicamentoService;
import com.rrparedes.neurosilogic.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final UsuarioService usuarioService;
    private final MedicamentoService medicamentoService;

    public AdminController(UsuarioService usuarioService, MedicamentoService medicamentoService) {
        this.usuarioService = usuarioService;
        this.medicamentoService = medicamentoService;
    }

    // Verifica sesión activa y rol de administrador; retorna la ruta de redirección si el acceso
    // debe rechazarse, o null si el usuario puede continuar. Centraliza el chequeo que antes
    // estaba duplicado en cada endpoint de este controlador.
    private String verificarAcceso(HttpSession session) {
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");
        if (userLog == null) return "redirect:/login";
        if (!userLog.isAdministrador()) return "redirect:/dashboard";
        return null;
    }

    // ── Gestión de Usuarios ──
    @GetMapping("/gestionar-usuarios")
    public String gestionarUsuarios(HttpSession session, Model model) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;

        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "gestionar_usuarios";
    }

    @PostMapping("/gestionar-usuarios/bloquear")
    public String cambiarEstadoUsuario(@RequestParam Long idUsuario, @RequestParam String nuevoEstado,
                                       HttpSession session, RedirectAttributes redirectAttributes) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        usuarioService.cambiarEstado(userLog, idUsuario, nuevoEstado);
        redirectAttributes.addFlashAttribute("mensaje",
                "B".equalsIgnoreCase(nuevoEstado) ? "Usuario bloqueado exitosamente." : "Usuario activado/desbloqueado exitosamente.");
        return "redirect:/gestionar-usuarios";
    }

    @PostMapping("/gestionar-usuarios/asignar-rol")
    public String asignarRolUsuario(@RequestParam Long idUsuario, @RequestParam String nuevoRol,
                                    HttpSession session, RedirectAttributes redirectAttributes) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        usuarioService.asignarRol(userLog, idUsuario, nuevoRol);
        redirectAttributes.addFlashAttribute("mensaje", "Rol asignado exitosamente.");
        return "redirect:/gestionar-usuarios";
    }

    @PostMapping("/gestionar-usuarios/editar")
    public String editarUsuario(@RequestParam Long idUsuario,
                                @RequestParam String nombreUsuario,
                                @RequestParam String cedula,
                                @RequestParam String nombreCompleto,
                                @RequestParam String email,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        usuarioService.editarUsuario(userLog, idUsuario, nombreUsuario, cedula, nombreCompleto, email);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente.");
        return "redirect:/gestionar-usuarios";
    }

    // ── Gestión de Catálogo de Medicamentos ──
    @GetMapping("/gestionar-catalogo")
    public String gestionarCatalogo(HttpSession session, Model model) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;

        model.addAttribute("medicamentos", medicamentoService.listarTodos());
        return "gestionar_catalogo";
    }

    @PostMapping("/gestionar-catalogo/guardar")
    public String guardarMedicamento(@RequestParam String nombre,
                                     @RequestParam String composicion,
                                     @RequestParam String dosisRecomendada,
                                     @RequestParam Integer stock,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        medicamentoService.registrar(userLog, nombre, composicion, dosisRecomendada, stock);
        redirectAttributes.addFlashAttribute("mensaje", "Medicamento agregado al catálogo exitosamente.");
        return "redirect:/gestionar-catalogo";
    }

    @PostMapping("/gestionar-catalogo/eliminar")
    public String eliminarMedicamento(@RequestParam Long idMedicamento, HttpSession session, RedirectAttributes redirectAttributes) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        medicamentoService.eliminar(userLog, idMedicamento);
        redirectAttributes.addFlashAttribute("mensaje", "Medicamento eliminado del catálogo.");
        return "redirect:/gestionar-catalogo";
    }

    @PostMapping("/gestionar-catalogo/agregar-stock")
    public String agregarStockMedicamento(@RequestParam Long idMedicamento, @RequestParam Integer cantidad,
                                          HttpSession session, RedirectAttributes redirectAttributes) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        medicamentoService.agregarStock(userLog, idMedicamento, cantidad);
        redirectAttributes.addFlashAttribute("mensaje", "Stock actualizado exitosamente.");
        return "redirect:/gestionar-catalogo";
    }

    @PostMapping("/gestionar-catalogo/editar")
    public String editarMedicamento(@RequestParam Long idMedicamento,
                                    @RequestParam String nombre,
                                    @RequestParam String composicion,
                                    @RequestParam String dosisRecomendada,
                                    @RequestParam Integer stock,
                                    HttpSession session, RedirectAttributes redirectAttributes) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        medicamentoService.editar(userLog, idMedicamento, nombre, composicion, dosisRecomendada, stock);
        redirectAttributes.addFlashAttribute("mensaje", "Medicamento actualizado exitosamente.");
        return "redirect:/gestionar-catalogo";
    }
}
