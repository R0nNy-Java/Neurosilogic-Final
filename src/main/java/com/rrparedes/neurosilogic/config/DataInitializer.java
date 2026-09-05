package com.rrparedes.neurosilogic.config;

import com.rrparedes.neurosilogic.model.*;
import com.rrparedes.neurosilogic.repository.*;
import com.rrparedes.neurosilogic.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final EnfermedadRepository enfermedadRepository;
    private final RangoSignoNormalRepository rangoSignoNormalRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PacienteRepository pacienteRepository,
                           MedicamentoRepository medicamentoRepository,
                           EnfermedadRepository enfermedadRepository,
                           RangoSignoNormalRepository rangoSignoNormalRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.enfermedadRepository = enfermedadRepository;
        this.rangoSignoNormalRepository = rangoSignoNormalRepository;
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
            Paciente p1 = new Paciente("1723456789", "Juan Carlos", "Pérez Gómez", java.time.LocalDate.of(1979, 5, 15), "M", "A");
            Paciente p2 = new Paciente("1712345678", "Ana María", "Torres Silva", java.time.LocalDate.of(1992, 8, 20), "F", "A");
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

        if (enfermedadRepository.count() == 0) {
            Enfermedad e1 = enfermedadRepository.save(new Enfermedad(null, "Hipertensión Arterial Primaria (HTA)", "Cardiovascular", "Presión arterial persistentemente elevada. Sistólica esperada 120-139, Diastólica 80-89"));
            Enfermedad e2 = enfermedadRepository.save(new Enfermedad(null, "Diabetes Mellitus Tipo 2 (DM2)", "Endocrino", "Trastorno metabólico con hiperglicemia. Rango objetivo basal 70-130 mg/dL"));
            Enfermedad e3 = enfermedadRepository.save(new Enfermedad(null, "Enfermedad Pulmonar Obstructiva Crónica (EPOC)", "Respiratorio", "Obstrucción crónica del flujo aéreo. SatO2 permisiva esperada 88-92%"));
            Enfermedad e4 = enfermedadRepository.save(new Enfermedad(null, "Insuficiencia Cardíaca Crónica (ICC)", "Cardiovascular", "Disfunción de bomba cardíaca. Control estricto de FC (60-90 ppm) y sobrecarga de volumen/peso"));
            Enfermedad e5 = enfermedadRepository.save(new Enfermedad(null, "Asma Bronquial", "Respiratorio", "Inflamación hiperreactiva de vías aéreas. FR esperada 16-24 rpm, SatO2 > 94%"));
            Enfermedad e6 = enfermedadRepository.save(new Enfermedad(null, "Hipotiroidismo Primario", "Endocrino", "Deficiencia de hormonas tiroideas. Bradicardia relativa permisiva 55-75 ppm, tendencia a aumento ponderal"));
            Enfermedad e7 = enfermedadRepository.save(new Enfermedad(null, "Insuficiencia Renal Crónica (IRC)", "Renal", "Pérdida progresiva de función renal. Monitorización estricta de PA y peso seco"));
            Enfermedad e8 = enfermedadRepository.save(new Enfermedad(null, "Cardiopatía Isquémica / Angina", "Cardiovascular", "Disminución del flujo coronario. FC objetivo en reposo 50-80 ppm"));
            Enfermedad e9 = enfermedadRepository.save(new Enfermedad(null, "Neumonía Adquirida en la Comunidad", "Infeccioso", "Infección parenquimatosa pulmonar. Taquipnea permisiva 18-28 rpm, fiebre 36.5-38.5 °C"));
            Enfermedad e10 = enfermedadRepository.save(new Enfermedad(null, "Obesidad Mórbida (IMC >= 40)", "Metabólico", "Índice de masa corporal severamente elevado. Evaluación adaptativa de presión y mecánica respiratoria"));

            System.out.println(">>> Seed Data: 10 enfermedades clínicas inscritas.");

            // Rangos Fisiológicos Adaptativos por Enfermedad
            rangoSignoNormalRepository.save(new RangoSignoNormal(e1.getIdEnfermedad(), "Hipertensión (HTA)", 36.0, 37.5, 120, 139, 80, 89, 60, 95, 12, 20, 94, 100));
            rangoSignoNormalRepository.save(new RangoSignoNormal(e3.getIdEnfermedad(), "EPOC Adaptativo", 36.0, 37.5, 110, 135, 70, 85, 65, 95, 14, 24, 88, 92));
            rangoSignoNormalRepository.save(new RangoSignoNormal(e4.getIdEnfermedad(), "Insuficiencia Cardíaca", 36.0, 37.4, 100, 130, 60, 85, 55, 85, 14, 22, 92, 100));
            rangoSignoNormalRepository.save(new RangoSignoNormal(e6.getIdEnfermedad(), "Hipotiroidismo", 35.8, 37.2, 100, 130, 65, 85, 50, 75, 12, 18, 95, 100));
            rangoSignoNormalRepository.save(new RangoSignoNormal(e9.getIdEnfermedad(), "Neumonía Infecciosa", 36.5, 38.5, 105, 135, 65, 88, 70, 105, 16, 26, 91, 98));
        }

        if (rangoSignoNormalRepository.findByIdEnfermedadIsNull().isEmpty()) {
            // Rango Estándar de Persona Sana (Normal general)
            rangoSignoNormalRepository.save(new RangoSignoNormal(null, "Persona Sana Estándar", 36.0, 37.4, 90, 120, 60, 80, 60, 100, 12, 20, 95, 100));
            System.out.println(">>> Seed Data: Rango fisiológico estándar creado.");
        }
    }
}

