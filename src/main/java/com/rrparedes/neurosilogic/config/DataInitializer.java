package com.rrparedes.neurosilogic.config;

import com.rrparedes.neurosilogic.model.Medicamento;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.MedicamentoRepository;
import com.rrparedes.neurosilogic.repository.PacienteRepository;
import com.rrparedes.neurosilogic.repository.UsuarioRepository;
import com.rrparedes.neurosilogic.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicamentoRepository medicamentoRepository;

    public DataInitializer(UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository, MedicamentoRepository medicamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario("admin", PasswordUtil.hash("Admin1234"), "Super", "Administrador", "ADMINISTRADOR", "A");
            admin.setEmail("admin@nurselogic.ec");

            Usuario enfermero1 = new Usuario("enfermero1", PasswordUtil.hash("Nurse1234"), "Maria Fernanda", "Lopez", "ENFERMERO", "A");
            enfermero1.setEmail("mlopez@nurselogic.ec");

            Usuario enfermero2 = new Usuario("enfermero2", PasswordUtil.hash("Nurse5678"), "Carlos Eduardo", "Vega", "ENFERMERO", "A");
            enfermero2.setEmail("cvega@nurselogic.ec");

            usuarioRepository.save(admin);
            usuarioRepository.save(enfermero1);
            usuarioRepository.save(enfermero2);
            System.out.println(">>> Seed Data: 3 usuarios iniciales creados.");
        }

        if (pacienteRepository.count() == 0) {
            Paciente p1 = new Paciente("1723456789", "Juan Carlos", "Pérez Gómez", 45, "M", "A");
            Paciente p2 = new Paciente("1712345678", "Ana María", "Torres Silva", 32, "F", "A");
            pacienteRepository.save(p1);
            pacienteRepository.save(p2);
            System.out.println(">>> Seed Data: 2 pacientes iniciales creados.");
        }

        if (medicamentoRepository.count() == 0) {
            medicamentoRepository.save(new Medicamento("Paracetamol 500mg", "Analgésico / Antipirético", "500 mg cada 8 horas"));
            medicamentoRepository.save(new Medicamento("Ibuprofeno 400mg", "Antiinflamatorio no esteroideo", "400 mg cada 8 horas con alimentos"));
            medicamentoRepository.save(new Medicamento("Omeprazol 20mg", "Protector gástrico", "20 mg en ayunas"));
            System.out.println(">>> Seed Data: 3 medicamentos iniciales creados.");
        }
    }
}
