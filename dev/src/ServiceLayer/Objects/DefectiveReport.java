package ServiceLayer.Objects;

import java.util.List;

public class DefectiveReport extends Report{

    private List<ServiceLayer.Objects.ProductItem> DefectivePro;

    public DefectiveReport(BusinessLayer.DefectiveReport report) {
        super(report);
        List<BusinessLayer.ProductItem> BusinessDefectivePro=report.getDefectivePro();
        for (BusinessLayer.ProductItem p:BusinessDefectivePro) {
            DefectivePro.add(new ServiceLayer.Objects.ProductItem(p));
        }
    }
}
