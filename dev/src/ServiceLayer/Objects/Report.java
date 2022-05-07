package ServiceLayer.Objects;

import java.util.Date;

public class Report {

    Integer id;
    String name;
    Date date;
    String report_producer;

    public Report(BusinessLayer.Report report) {
        id= report.getId();
        name=report.getName();
        date= report.getDate();
        report_producer= report.getReport_producer();
    }

    public String getName() { return name; }
    public String toString(){
        return "Id: "+id+"\n"+ "name: "+name+"\n"+ "date: "+date+"\n"+ "report_producer: "+report_producer+"\n";
    }
}