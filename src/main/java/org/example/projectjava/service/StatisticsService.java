package org.example.projectjava.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.example.projectjava.Model.DepartmentMembers.DepartmentMembersService;
import org.example.projectjava.Model.Member.MemberService;
import org.example.projectjava.Model.MemberDetails.MemberDetailsService;
import org.example.projectjava.Model.Task.TasksService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    DepartmentMembersService departmentMembersService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberDetailsService memberDetailsService;
    @Autowired
    private TasksDepartmentsService tasksDepartmentsService;
    @Autowired
    private ActivityHoursMembersService activityHoursMembersService;

    private int nrMembri;
    private int itMembers;
    private int prmMembers;
    private int proMembers;
    private int reMembers;
    private int riMembers;
    private int evMembers;
    @Autowired
    private TasksService tasksService;

    @PostConstruct
    public void init() {
        nrMembri = memberService.getAllMembersCount();
        itMembers = departmentMembersService.getMembERsCountByDepartmentName("IT");
        prmMembers = departmentMembersService.getMembERsCountByDepartmentName("PR&Media");
        proMembers = departmentMembersService.getMembERsCountByDepartmentName("Proiecte");
        reMembers = departmentMembersService.getMembERsCountByDepartmentName("Relatii Externe");
        riMembers = departmentMembersService.getMembERsCountByDepartmentName("Relatii Interne");
        evMembers = departmentMembersService.getMembERsCountByDepartmentName("Evaluari");
    }

    public byte[] createPdfReport() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.addTitle("Statistici ASII");
            document.addCreationDate();

            document.add(new Paragraph("Numar total de membri: " + nrMembri));
            document.add(new Paragraph("\n"));

            departmentMembersNumber(document);

            Image membersRightVote = statisticsMembersVotingRight();
            membersRightVote.scaleToFit(420, 280);
            membersRightVote.setAlignment(Element.ALIGN_CENTER);
            document.add(membersRightVote);

            Image membersDistribution = statisticsMembersDistribution();
            membersDistribution.scaleToFit(420, 280);
            membersDistribution.setAlignment(Element.ALIGN_CENTER);
            document.add(membersDistribution);

            Image topDepartments = topDepartments();
            topDepartments.scaleToFit(420, 280);
            topDepartments.setAlignment(Element.ALIGN_CENTER);
            document.add(topDepartments);

            Image tasks = activityPeriod();
            tasks.scaleToFit(420, 280);
            tasks.setAlignment(Element.ALIGN_CENTER);
            document.add(tasks);

            document.add(new Paragraph("\n"));
            Paragraph paragraph = new Paragraph("Top members");
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph("\n"));

            topMembers(document);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void departmentMembersNumber(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.addCell(new PdfPCell(new Phrase("Departament")));
        table.addCell(new PdfPCell(new Phrase("Numar membri")));
        table.addCell("Total membri");
        table.addCell(String.valueOf(nrMembri));
        table.addCell("IT");
        table.addCell(String.valueOf(itMembers));
        table.addCell("PR&Media");
        table.addCell(String.valueOf(prmMembers));
        table.addCell("Proiecte");
        table.addCell(String.valueOf(proMembers));
        table.addCell("Relatii Externe");
        table.addCell(String.valueOf(reMembers));
        table.addCell("Relatii Interne");
        table.addCell(String.valueOf(riMembers));
        table.addCell("Evaluari");
        table.addCell(String.valueOf(evMembers));

        document.add(table);
    }

    private Image statisticsMembersDistribution() {

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("IT", itMembers);
        dataset.setValue("PR&Media", prmMembers);
        dataset.setValue("Proiecte", proMembers);
        dataset.setValue("Relatii Externe", reMembers);
        dataset.setValue("Relatii Interne", riMembers);
        dataset.setValue("Evaluari", evMembers);

        JFreeChart chart = org.jfree.chart.ChartFactory.createPieChart(
                "Statistici Distributie Membri",
                dataset,
                true,
                true,
                false
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, chart, 400, 300);
            return (Image.getInstance(baos.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Image statisticsMembersVotingRight() {
        long haveVotingRight = memberDetailsService.countMembersVotingRight("DA");
        long notHaveVotingRight = memberDetailsService.countMembersVotingRight("NU");

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Au drept de vot", haveVotingRight);
        dataset.setValue("Nu au drept de vot", notHaveVotingRight);

        JFreeChart chart = org.jfree.chart.ChartFactory.createPieChart(
                "Statistici Membri Drept De Vot",
                dataset,
                true,
                true,
                false
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, chart, 400, 300);
            return (Image.getInstance(baos.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Image topDepartments() {
        Map<String, Integer> departmentsTasks = tasksDepartmentsService.tasksDepartments();

        List<Map.Entry<String, Integer>> topDepartments = departmentsTasks.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : topDepartments) {
            dataset.addValue(entry.getValue(), "Taskuri", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Top Departamente",
                "Departament",
                "Numar Taskuri",
                dataset
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, chart, 400, 300);
            return (Image.getInstance(baos.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Image activityPeriod() {
        Map<LocalDate, Long> tasksPerDays = tasksService.tasksPerDay();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        tasksPerDays.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String date = entry.getKey().toString();
                    dataset.addValue(entry.getValue(), "Taskuri", date);
                });

        JFreeChart chart = ChartFactory.createBarChart(
                "Activitate Taskuri",
                "Perioada",
                "Numar Taskuri",
                dataset
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, chart, 400, 300);
            return (Image.getInstance(baos.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void topMembers(Document document) throws DocumentException {
        Map<String, Float> members = activityHoursMembersService.activityHoursMembers();
        List<Map.Entry<String, Float>> topMembers = members.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                .toList();

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);

        PdfPCell cell1 = new PdfPCell(new Paragraph("Membru", headerFont));
        PdfPCell cell2 = new PdfPCell(new Paragraph("Ore de activitate", headerFont));
        table.addCell(cell1);
        table.addCell(cell2);

        for (Map.Entry<String, Float> entry : topMembers) {
            table.addCell(new PdfPCell(new Paragraph(entry.getKey(), cellFont)));
            table.addCell(new PdfPCell(new Paragraph(String.format("%.2f", entry.getValue()), cellFont)));
        }

        document.add(table);
    }

}
