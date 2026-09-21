package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.model.Ingresso;
import br.com.gerenciamentoteatro.model.RelatorioFinanceiroPeca;
import br.com.gerenciamentoteatro.model.RelatorioFinanceiroTeatro;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PDFGeneratorService {

    private static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
    private static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
    private static final Font FONT_TEXTO = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font FONT_TEXTO_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FMT_MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    /**
     * REQUISITO 13: Exportação do PDF do Relatório Financeiro por Peça.
     */
    public static void gerarRelatorioFinanceiroPecaPDF(RelatorioFinanceiroPeca relatorio, String caminhoArquivo) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        Paragraph titulo = new Paragraph("Relatório Financeiro da Peça", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        adicionarLinhaTabela(table, "Nome da Peça:", relatorio.getNomePeca());
        adicionarLinhaTabela(table, "Artista Locatário:", relatorio.getNomeArtista());
        adicionarLinhaTabela(table, "Total Arrecadado (Ingressos):", FMT_MOEDA.format(relatorio.getArrecadacaoIngressos()));
        adicionarLinhaTabela(table, "Custo do Aluguel do Teatro:", FMT_MOEDA.format(relatorio.getCustoAluguel()));
        adicionarLinhaTabela(table, "Valor Líquido de Repasse:", FMT_MOEDA.format(relatorio.getValorLiquidoRepasse()));

        document.add(table);
        document.close();
    }

    /**
     * REQUISITO 16: Exportação do PDF do Relatório Geral do Teatro.
     */
    public static void gerarRelatorioFinanceiroTeatroPDF(RelatorioFinanceiroTeatro relatorio, String caminhoArquivo) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        Paragraph titulo = new Paragraph("Relatório Financeiro Geral do Teatro", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        String periodo = "Período: " + relatorio.getDataInicio().format(FMT_DATA) + " até " + relatorio.getDataFim().format(FMT_DATA);
        Paragraph sub = new Paragraph(periodo, FONT_SUBTITULO);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(20);
        document.add(sub);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        adicionarLinhaTabela(table, "Receita com Venda de Ingressos:", FMT_MOEDA.format(relatorio.getTotalVendaIngressos()));
        adicionarLinhaTabela(table, "Receita com Aluguéis de Teatro:", FMT_MOEDA.format(relatorio.getTotalContratosAluguel()));
        adicionarLinhaTabela(table, "Receita Geral Total:", FMT_MOEDA.format(relatorio.getReceitaTotalGeral()));

        document.add(table);
        document.close();
    }

    /**
     * REQUISITO 8: Exportação do PDF do Contrato de Aluguel.
     */
    public static void gerarContratoAluguelPDF(ContratoAluguel contrato, String caminhoArquivo) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        Paragraph titulo = new Paragraph("Contrato de Aluguel de Teatro", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        adicionarLinhaTabela(table, "Nº do Contrato:", String.valueOf(contrato.getId()));
        adicionarLinhaTabela(table, "Artista Responsável:", contrato.getProposta().getArtista().getNomeCompleto());
        adicionarLinhaTabela(table, "CPF do Artista:", contrato.getProposta().getArtista().getCpf());
        adicionarLinhaTabela(table, "Nome da Peça:", contrato.getProposta().getPeca().getNome());
        adicionarLinhaTabela(table, "Início da Exibição:", contrato.getDataInicio().format(FMT_DATA));
        adicionarLinhaTabela(table, "Fim da Exibição:", contrato.getDataFim().format(FMT_DATA));
        adicionarLinhaTabela(table, "Valor Total do Aluguel:", FMT_MOEDA.format(contrato.getValorTotal()));

        document.add(table);
        document.close();
    }

    /**
     * REQUISITO 11: Exportação do Bilhete de Ingresso em PDF.
     */
    public static void gerarIngressoPDF(Ingresso ingresso, String caminhoArquivo) throws Exception {
        Document document = new Document(PageSize.A6, 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        Paragraph titulo = new Paragraph("INGRESSO DE TEATRO", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(15);
        document.add(titulo);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        adicionarLinhaTabela(table, "Espectador:", ingresso.getCliente().getNomeCompleto());
        adicionarLinhaTabela(table, "CPF:", ingresso.getCliente().getCpf());
        adicionarLinhaTabela(table, "Peça:", ingresso.getPeca().getNome());
        adicionarLinhaTabela(table, "Data de Exibição:", ingresso.getDataExibicao().format(FMT_DATA));
        adicionarLinhaTabela(table, "Quantidade:", String.valueOf(ingresso.getQuantidade()));

        document.add(table);
        document.close();
    }

    private static void adicionarLinhaTabela(PdfPTable table, String rotulo, String valor) {
        PdfPCell cell1 = new PdfPCell(new Phrase(rotulo, FONT_TEXTO_BOLD));
        PdfPCell cell2 = new PdfPCell(new Phrase(valor != null ? valor : "-", FONT_TEXTO));
        cell1.setPadding(6);
        cell2.setPadding(6);
        table.addCell(cell1);
        table.addCell(cell2);
    }
}