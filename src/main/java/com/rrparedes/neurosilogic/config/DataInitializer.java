package com.rrparedes.neurosilogic.config;

import com.rrparedes.neurosilogic.model.*;
import com.rrparedes.neurosilogic.repository.*;
import com.rrparedes.neurosilogic.service.*;
import com.rrparedes.neurosilogic.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final EnfermedadRepository enfermedadRepository;
    private final RangoSignoNormalRepository rangoSignoNormalRepository;
    private final PacienteService pacienteService;
    private final ModuloClinicoService moduloClinicoService;
    private final DosificacionService dosificacionService;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PacienteRepository pacienteRepository,
                           MedicamentoRepository medicamentoRepository,
                           EnfermedadRepository enfermedadRepository,
                           RangoSignoNormalRepository rangoSignoNormalRepository,
                           PacienteService pacienteService,
                           ModuloClinicoService moduloClinicoService,
                           DosificacionService dosificacionService) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.enfermedadRepository = enfermedadRepository;
        this.rangoSignoNormalRepository = rangoSignoNormalRepository;
        this.pacienteService = pacienteService;
        this.moduloClinicoService = moduloClinicoService;
        this.dosificacionService = dosificacionService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Usuarios Iniciales
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

        // 2. Medicamentos Iniciales
        if (medicamentoRepository.count() == 0) {
            medicamentoRepository.save(new Medicamento("Paracetamol 500mg", "Analgésico / Antipirético", "500 mg cada 8 horas"));
            medicamentoRepository.save(new Medicamento("Ibuprofeno 400mg", "Antiinflamatorio no esteroideo", "400 mg cada 8 horas con alimentos"));
            medicamentoRepository.save(new Medicamento("Omeprazol 20mg", "Protector gástrico", "20 mg en ayunas"));
            medicamentoRepository.save(new Medicamento("Amoxicilina 500mg", "Antibiótico de amplio espectro", "500 mg cada 8 horas por 7 días"));
            medicamentoRepository.save(new Medicamento("Enalapril 10mg", "Antihipertensivo IECA", "10 mg cada 12 horas"));
            System.out.println(">>> Seed Data: Medicamentos iniciales creados.");
        }

        // 3. Enfermedades e Infecciones Clínicas
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
            rangoSignoNormalRepository.save(new RangoSignoNormal(null, "Persona Sana Estándar", 36.0, 37.4, 90, 120, 60, 80, 60, 100, 12, 20, 95, 100));
            System.out.println(">>> Seed Data: Rango fisiológico estándar creado.");
        }

        // 4. Generación Dinámica de 50 Pacientes con Historial Clínico Completo
        if (pacienteRepository.count() < 50) {
            System.out.println(">>> Poblando base de datos con 50 pacientes reales y sus historiales clínicos completos...");

            String[] nombresHombres = {"Luis Miguel", "Carlos Alberto", "Andrés Felipe", "Mateo Alejandro", "Gabriel Eduardo", "Diego Fernando", "Jorge Luis", "Ricardo Antonio", "Sebastián José", "Nicolás Esteban", "Christian Paul", "Pablo Javier", "Francisco José", "Gonzalo Ramón", "David Alexander", "Daniel Marcelo", "José Manuel", "Víctor Hugo", "Manuel Ignacio", "Santiago Gabriel", "Esteban Daniel", "Javier Alejandro", "Roberto Carlos", "Fernando José", "Alejandro Mateo"};
            String[] nombresMujeres = {"María Fernanda", "Ana Lucía", "Sofia Elizabeth", "Elena Beatriz", "Camila Alejandra", "Daniela Isabel", "Patricia Andrea", "Carmen Rosa", "Verónica Patricia", "Mónica Alexandra", "Natalia Carolina", "Diana Marcela", "Jessica Paola", "Silvia Lorena", "Claudia Maria", "Gabriela Cristina", "Isabel Sofia", "Valeria Nicole", "Lucia Fernanda", "Cristina Elizabeth", "Adriana Paola", "Johana Maria", "Lorena Patricia", "Ximena Alexandra", "Evelyn Carolina"};

            String[] apellidosList = {"Salazar", "Mora", "Castillo", "Mendoza", "Morales", "Romero", "Guerrero", "Paredes", "Viteri", "Espinosa", "Andrade", "Cárdenas", "Benítez", "Jaramillo", "Aguilar", "Narváez", "Villacís", "Barreno", "Freire", "Cobo", "Torres", "Silva", "Gómez", "López", "Vega"};

            List<Usuario> usuarios = usuarioRepository.findAll();
            Usuario enfermero1 = usuarios.stream().filter(u -> "enfermero1".equals(u.getNombreUsuario())).findFirst().orElse(usuarios.get(0));
            Usuario enfermero2 = usuarios.stream().filter(u -> "enfermero2".equals(u.getNombreUsuario())).findFirst().orElse(usuarios.get(0));

            List<Enfermedad> enfermedades = enfermedadRepository.findAll();

            long existentes = pacienteRepository.count();
            int aGenerar = 50 - (int) existentes;

            for (int i = 0; i < aGenerar; i++) {
                String sexo = (i % 2 == 0) ? "M" : "F";
                String nombre = (sexo.equals("M")) ? nombresHombres[i % nombresHombres.length] : nombresMujeres[i % nombresMujeres.length];
                String apellido1 = apellidosList[i % apellidosList.length];
                String apellido2 = apellidosList[(i + 7) % apellidosList.length];
                String apellidosCompletos = apellido1 + " " + apellido2;

                // Edad entre 19 y 60 años
                int edad = 19 + (i * 37) % 42; 
                LocalDate fechaNac = LocalDate.now().minusYears(edad).minusDays(i * 11 % 300);

                // Generar Cédula Ecuatoriana Válida (Módulo 10)
                String cedula = generarCedulaValida(i);

                // Estado: la gran mayoría 'A' (Activos), y unos 3 con 'I' (Alta/Inactivos) para pruebas
                String estado = (i == 5 || i == 15 || i == 25) ? "I" : "A";

                Paciente p = new Paciente(cedula, nombre, apellidosCompletos, fechaNac, sexo, estado);
                p = pacienteRepository.save(p);

                // Asignar Antecedente / Enfermedad si aplica
                if (i % 5 != 0 && !enfermedades.isEmpty()) {
                    Enfermedad enf = enfermedades.get(i % enfermedades.size());
                    moduloClinicoService.registrarAntecedente(p.getIdPaciente(), "PATOLÓGICO", enf.getNombreEnfermedad(), "Paciente diagnosticado previamente con " + enf.getNombreEnfermedad() + ".");
                } else {
                    moduloClinicoService.registrarAntecedente(p.getIdPaciente(), "GENERAL", "Ninguna", "Sin antecedentes crónicos de importancia.");
                }

                // Generar Signos Vitales realistas
                int sistolica = 110 + (i * 7) % 45;
                int diastolica = 70 + (i * 4) % 25;
                int fc = 60 + (i * 9) % 45;
                int fr = 14 + (i * 3) % 12;
                double temp = 36.2 + ((i % 5) * 0.4);
                int sat = 90 + (i * 5) % 10;
                moduloClinicoService.registrarSignoVital(p.getIdPaciente(), sistolica, diastolica, fc, fr, temp, sat);

                // Generar Glasgow
                int ocular = 3 + (i % 2);
                int verbal = 4 + (i % 2);
                int motora = 5 + (i % 2);
                moduloClinicoService.registrarGlasgow(p.getIdPaciente(), ocular, verbal, motora);

                // Generar IMC
                double peso = 55.0 + (i * 1.5) % 45.0;
                double estatura = 1.55 + (i * 0.01) % 0.30;
                moduloClinicoService.registrarIMC(p.getIdPaciente(), peso, estatura);

                // Dosificación si aplica
                if (i % 2 == 0) {
                    dosificacionService.registrar(p.getIdPaciente(), "Paracetamol 500mg", 500.0, "mg", 500.0, "mg", 10.0, 8.0);
                }

                // Cierre de Ficha en Kanban para ~80% de los pacientes
                if (i % 4 != 0) {
                    Usuario enfermeroResponsable = (i % 2 == 0) ? enfermero1 : enfermero2;
                    pacienteService.registrarCierreFicha(p.getIdPaciente(), enfermeroResponsable);
                }
            }

            System.out.println(">>> Seed Data: 50 pacientes con historial clínico completo generados exitosamente.");
        }
    }

    /**
     * Generador de Cédulas Ecuatorianas Válidas mediante Algoritmo Módulo 10.
     */
    private String generarCedulaValida(int indice) {
        // Códigos de provincia válidos de Ecuador (01 a 24)
        int provNum = 1 + (indice % 24);
        String provStr = String.format("%02d", provNum);

        int d3 = (indice % 6); // 0 a 5
        int d4 = (indice * 3) % 10;
        int d5 = (indice * 7) % 10;
        int d6 = (indice * 2) % 10;
        int d7 = (indice * 5) % 10;
        int d8 = (indice * 8) % 10;
        int d9 = (indice * 4) % 10;

        String base9 = provStr + d3 + d4 + d5 + d6 + d7 + d8 + d9;

        // Calcular dígito verificador módulo 10
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int k = 0; k < 9; k++) {
            int val = Character.getNumericValue(base9.charAt(k)) * coeficientes[k];
            if (val >= 10) val -= 9;
            suma += val;
        }
        int residuo = suma % 10;
        int verificador = (residuo == 0) ? 0 : (10 - residuo);

        return base9 + verificador;
    }
}
