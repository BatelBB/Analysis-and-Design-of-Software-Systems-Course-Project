package BusinessLayer;

import java.util.List;

public class byProductReport extends Report {

    private List<ProductItem> byProductPro;
    private String proName;

    public byProductReport(String name, Integer id, String report_producer, List<ProductItem> byProductPro, String proName) {
        super(name, id, report_producer);
        this.byProductPro=byProductPro;
        this.proName=proName;
    }

    public List<ProductItem> getByProductPro() { return byProductPro; }

    public String getProName() {return proName;}
}