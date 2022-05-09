package groupk.logistics.DataLayer;

import groupk.logistics.business.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverLicencesMapper extends myDataBase {
    private DriverLicencesIDMapper driverLicencesIDMapper;
    public DriverLicencesMapper() throws Exception {
        super("Driver_Licences");
        driverLicencesIDMapper = DriverLicencesIDMapper.getInstance();
    }



    @Override
    Object ConvertResultSetToDTO(ResultSet rs) throws SQLException {
        return rs.getString(2);
    }

    public List<String> getMyLicenses(String username) {
        List DTOList = new LinkedList();
        String query = "SELECT * FROM Drivers_Licences Where username = " + username;
        try (Connection conn = getConnection();
             PreparedStatement pstmt  = conn.prepareStatement(query)){
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                DTOList.add(ConvertResultSetToDTO(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return DTOList;
    }

    public boolean addLicence(String username, String license) {

        int n = 0;
        String query = "INSERT INTO Drivers_Licences(username,licence) VALUES(?,?)";

        try(Connection conn = getConnection()){
            if(conn != null) {
                PreparedStatement prepStat = conn.prepareStatement(query);
                prepStat.setString(1, username);
                prepStat.setString(2, license);
                n = prepStat.executeUpdate();
                driverLicencesIDMapper.driverLicencesIDMapper.put(username,license);
            }
            else
                return false;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return n == 1;
    }
}
