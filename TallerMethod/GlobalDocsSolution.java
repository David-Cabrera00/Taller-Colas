import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DocumentProcessingApp extends JFrame {

    private final JComboBox<Country> countryBox = new JComboBox<>(Country.values());
    private final JComboBox<DocumentType> typeBox = new JComboBox<>(DocumentType.values());
    private final JComboBox<DocumentFormat> formatBox = new JComboBox<>(DocumentFormat.values());

    private final JTextField fileNameField = new JTextField();
    private final JTextArea contentArea = new JTextArea(4, 30);

    private final DefaultListModel<String> batchModel = new DefaultListModel<>();
    private final JList<String> batchList = new JList<>(batchModel);

    private final JTextArea outputArea = new JTextArea();

    private final List<DocumentRequest> batch = new ArrayList<>();
    private final ProcessManager manager = new EnterpriseProcessManager();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DocumentProcessingApp().setVisible(true));
    }

    public DocumentProcessingApp() {
        setTitle("Sistema de Procesamiento de Documentos Empresariales");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Sistema de Procesamiento de Documentos Empresariales");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Patrón Factory Method | Interfaz en español");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.add(title);
        header.add(subtitle);

        main.add(header, BorderLayout.NORTH);
        main.add(buildFormPanel(), BorderLayout.CENTER);
        main.add(buildBottomPanel(), BorderLayout.SOUTH);

        return main;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Registro del documento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("País:"), gbc);
        gbc.gridx = 1;
        form.add(countryBox, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Tipo de documento:"), gbc);
        gbc.gridx = 3;
        form.add(typeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Formato:"), gbc);
        gbc.gridx = 1;
        form.add(formatBox, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Nombre del archivo:"), gbc);
        gbc.gridx = 3;
        form.add(fileNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Contenido / descripción:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(contentArea), gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Agregar al lote");
        JButton processButton = new JButton("Procesar lote");
        JButton examplesButton = new JButton("Cargar ejemplos");
        JButton clearButton = new JButton("Limpiar");

        addButton.addActionListener(e -> addToBatch());
        processButton.addActionListener(e -> processBatch());
        examplesButton.addActionListener(e -> loadExamples());
        clearButton.addActionListener(e -> clearAll());

        actions.add(addButton);
        actions.add(processButton);
        actions.add(examplesButton);
        actions.add(clearButton);

        panel.add(form, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JSplitPane buildBottomPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Lote pendiente"));
        left.add(new JScrollPane(batchList), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Resultados"));
        right.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.35);
        split.setPreferredSize(new Dimension(950, 330));
        return split;
    }

    private void addToBatch() {
        String fileName = fileNameField.getText().trim();
        String content = contentArea.getText().trim();
        Country country = (Country) countryBox.getSelectedItem();
        DocumentType type = (DocumentType) typeBox.getSelectedItem();
        DocumentFormat format = (DocumentFormat) formatBox.getSelectedItem();

        if (fileName.isBlank() || content.isBlank() || country == null || type == null || format == null) {
            showMessage("Debe completar todos los campos.");
            return;
        }

        if (!fileName.toLowerCase().endsWith("." + format.name().toLowerCase())) {
            showMessage("La extensión del archivo no coincide con el formato seleccionado.");
            return;
        }

        DocumentRequest request = new DocumentRequest(
                UUID.randomUUID().toString(),
                fileName,
                country,
                type,
                format,
                content
        );

        batch.add(request);
        batchModel.addElement(fileName + " | " + country + " | " + type + " | " + format);
        clearForm();
        showMessage("Documento agregado al lote.");
    }

    private void processBatch() {
        if (batch.isEmpty()) {
            showMessage("No hay documentos para procesar.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        int ok = 0;
        int error = 0;

        for (DocumentRequest request : batch) {
            ProcessingResult result = manager.process(request);

            sb.append(result.success() ? "✔ " : "✘ ")
                    .append(request.fileName())
                    .append(" | ")
                    .append(result.message())
                    .append(" | ")
                    .append(result.time().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .append("\n");

            if (result.success()) ok++;
            else error++;
        }

        sb.append("\n===== RESUMEN =====\n");
        sb.append("Correctos: ").append(ok).append("\n");
        sb.append("Con error: ").append(error).append("\n");
        sb.append("Total: ").append(ok + error);

        outputArea.setText(sb.toString());
        batch.clear();
        batchModel.clear();
    }

    private void loadExamples() {
        clearAll();

        batch.add(new DocumentRequest(UUID.randomUUID().toString(),
                "factura_colombia.pdf", Country.COLOMBIA, DocumentType.INVOICE, DocumentFormat.PDF,
                "Factura electrónica DIAN con total del servicio y NIT del cliente"));

        batch.add(new DocumentRequest(UUID.randomUUID().toString(),
                "certificado_mexico.txt", Country.MEXICO, DocumentType.DIGITAL_CERTIFICATE, DocumentFormat.TXT,
                "Certificado digital del SAT con firma electrónica vigente"));

        batch.add(new DocumentRequest(UUID.randomUUID().toString(),
                "declaracion_argentina.csv", Country.ARGENTINA, DocumentType.TAX_DECLARATION, DocumentFormat.CSV,
                "Declaracion tributaria presentada ante AFIP con impuesto anual"));

        batch.add(new DocumentRequest(UUID.randomUUID().toString(),
                "reporte_chile.xlsx", Country.CHILE, DocumentType.FINANCIAL_REPORT, DocumentFormat.XLSX,
                "Reporte con balance general e ingresos reportados al SII"));

        for (DocumentRequest request : batch) {
            batchModel.addElement(request.fileName() + " | " + request.country() + " | " + request.type() + " | " + request.format());
        }

        showMessage("Ejemplos cargados.");
    }

    private void clearAll() {
        clearForm();
        batch.clear();
        batchModel.clear();
        outputArea.setText("");
    }

    private void clearForm() {
        fileNameField.setText("");
        contentArea.setText("");
        countryBox.setSelectedIndex(0);
        typeBox.setSelectedIndex(0);
        formatBox.setSelectedIndex(0);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}

enum Country {
    COLOMBIA("Colombia"),
    MEXICO("México"),
    ARGENTINA("Argentina"),
    CHILE("Chile");

    private final String label;

    Country(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

enum DocumentType {
    INVOICE("Factura electrónica"),
    LEGAL_CONTRACT("Contrato legal"),
    FINANCIAL_REPORT("Reporte financiero"),
    DIGITAL_CERTIFICATE("Certificado digital"),
    TAX_DECLARATION("Declaración tributaria");

    private final String label;

    DocumentType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

enum DocumentFormat {
    PDF, DOC, DOCX, MD, CSV, TXT, XLSX
}

record DocumentRequest(
        String id,
        String fileName,
        Country country,
        DocumentType type,
        DocumentFormat format,
        String content
) {}

record ProcessingResult(
        boolean success,
        String message,
        LocalDateTime time
) {}

interface DocumentProcessor {
    ProcessingResult process(DocumentRequest request);
}

abstract class ProcessManager {
    public ProcessingResult process(DocumentRequest request) {
        DocumentProcessor processor = createProcessor(request.type());
        return processor.process(request);
    }

    protected abstract DocumentProcessor createProcessor(DocumentType type);
}

class EnterpriseProcessManager extends ProcessManager {
    @Override
    protected DocumentProcessor createProcessor(DocumentType type) {
        return switch (type) {
            case INVOICE -> new InvoiceProcessor();
            case LEGAL_CONTRACT -> new LegalContractProcessor();
            case FINANCIAL_REPORT -> new FinancialReportProcessor();
            case DIGITAL_CERTIFICATE -> new DigitalCertificateProcessor();
            case TAX_DECLARATION -> new TaxDeclarationProcessor();
        };
    }
}

abstract class BaseProcessor implements DocumentProcessor {

    private final String label;
    private final Set<DocumentFormat> allowedFormats;

    protected BaseProcessor(String label, Set<DocumentFormat> allowedFormats) {
        this.label = label;
        this.allowedFormats = allowedFormats;
    }

    @Override
    public ProcessingResult process(DocumentRequest request) {
        List<String> errors = new ArrayList<>();

        if (!allowedFormats.contains(request.format())) {
            errors.add("Formato no permitido para " + label + ".");
        }

        if (request.content() == null || request.content().isBlank()) {
            errors.add("El contenido no puede estar vacío.");
        }

        CountryRules.validate(request, errors);
        validateContent(request, errors);

        if (!errors.isEmpty()) {
            return new ProcessingResult(false, String.join(" ", errors), LocalDateTime.now());
        }

        return new ProcessingResult(true,
                label + " procesado correctamente para " + request.country() + ".",
                LocalDateTime.now());
    }

    protected abstract void validateContent(DocumentRequest request, List<String> errors);

    protected boolean containsAny(String text, String... words) {
        String value = text.toLowerCase();
        for (String word : words) {
            if (value.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

class InvoiceProcessor extends BaseProcessor {
    public InvoiceProcessor() {
        super("Factura", EnumSet.of(DocumentFormat.PDF, DocumentFormat.DOCX, DocumentFormat.XLSX, DocumentFormat.CSV));
    }

    @Override
    protected void validateContent(DocumentRequest request, List<String> errors) {
        if (!containsAny(request.content(), "total", "monto")) {
            errors.add("La factura debe incluir TOTAL o MONTO.");
        }
    }
}

class LegalContractProcessor extends BaseProcessor {
    public LegalContractProcessor() {
        super("Contrato legal", EnumSet.of(DocumentFormat.PDF, DocumentFormat.DOC, DocumentFormat.DOCX, DocumentFormat.TXT));
    }

    @Override
    protected void validateContent(DocumentRequest request, List<String> errors) {
        if (!containsAny(request.content(), "firma", "partes")) {
            errors.add("El contrato debe incluir FIRMA o PARTES.");
        }
    }
}

class FinancialReportProcessor extends BaseProcessor {
    public FinancialReportProcessor() {
        super("Reporte financiero", EnumSet.of(DocumentFormat.PDF, DocumentFormat.XLSX, DocumentFormat.CSV));
    }

    @Override
    protected void validateContent(DocumentRequest request, List<String> errors) {
        if (!containsAny(request.content(), "balance", "ingresos", "estado financiero")) {
            errors.add("El reporte financiero debe incluir BALANCE o INGRESOS.");
        }
    }
}

class DigitalCertificateProcessor extends BaseProcessor {
    public DigitalCertificateProcessor() {
        super("Certificado digital", EnumSet.of(DocumentFormat.PDF, DocumentFormat.TXT, DocumentFormat.MD));
    }

    @Override
    protected void validateContent(DocumentRequest request, List<String> errors) {
        if (!containsAny(request.content(), "digital", "firma")) {
            errors.add("El certificado digital debe incluir DIGITAL o FIRMA.");
        }
    }
}

class TaxDeclarationProcessor extends BaseProcessor {
    public TaxDeclarationProcessor() {
        super("Declaración tributaria", EnumSet.of(DocumentFormat.PDF, DocumentFormat.CSV, DocumentFormat.TXT, DocumentFormat.XLSX));
    }

    @Override
    protected void validateContent(DocumentRequest request, List<String> errors) {
        if (!containsAny(request.content(), "impuesto", "declaracion", "tributaria")) {
            errors.add("La declaración tributaria debe incluir IMPUESTO o DECLARACION.");
        }
    }
}

class CountryRules {

    private CountryRules() {}

    public static void validate(DocumentRequest request, List<String> errors) {
        String text = request.content().toLowerCase();

        switch (request.country()) {
            case COLOMBIA -> {
                if (request.type() == DocumentType.INVOICE && !text.contains("dian")) {
                    errors.add("En Colombia la factura debe incluir DIAN.");
                }
                if (request.type() == DocumentType.TAX_DECLARATION && !text.contains("nit")) {
                    errors.add("En Colombia la declaración debe incluir NIT.");
                }
            }
            case MEXICO -> {
                if (request.type() == DocumentType.INVOICE && !text.contains("rfc")) {
                    errors.add("En México la factura debe incluir RFC.");
                }
                if (request.type() == DocumentType.DIGITAL_CERTIFICATE && !text.contains("sat")) {
                    errors.add("En México el certificado debe incluir SAT.");
                }
            }
            case ARGENTINA -> {
                if (request.type() == DocumentType.INVOICE && !text.contains("cuit")) {
                    errors.add("En Argentina la factura debe incluir CUIT.");
                }
                if (request.type() == DocumentType.TAX_DECLARATION && !text.contains("afip")) {
                    errors.add("En Argentina la declaración debe incluir AFIP.");
                }
            }
            case CHILE -> {
                if (request.type() == DocumentType.FINANCIAL_REPORT && !text.contains("sii")) {
                    errors.add("En Chile el reporte debe incluir SII.");
                }
                if (request.type() == DocumentType.TAX_DECLARATION && !text.contains("rut")) {
                    errors.add("En Chile la declaración debe incluir RUT.");
                }
            }
        }
    }
}