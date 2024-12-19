package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {

    private static final Logger LOGGER = Logger.getLogger(MySqlProfileDao.class.getName());

    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {

        String sql = """
            INSERT INTO profiles (user_id, first_name, last_name, phone,
            email, address, city, state, zip)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return profile;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating profile", e);
            throw new RuntimeException("Failed to create profile", e);
        }
        return null;
    }

    @Override
    public Profile getByUserId(int userId) {

        String sql = "SELECT * FROM easyshop.profiles WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToProfile(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching profile by userId: " + userId, e);
            throw new RuntimeException("Failed to fetch profile", e);
        }
        return null;
    }

    @Override
    public List<Profile> getAllProfile() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM easyshop.profiles";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                profiles.add(mapToProfile(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all profiles", e);
            throw new RuntimeException("Failed to fetch profiles", e);
        }
        return profiles;
    }

    @Override
    public void updateProfile (Profile profile) {

        String sql = """
                UPDATE profiles SET first_name = ?, last_name = ?, 
                phone = ?, email = ?, address = ?, city = ?, state = ?, zip = ?
                WHERE user_id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, profile.getFirstName());
            preparedStatement.setString(2, profile.getLastName());
            preparedStatement.setString(3, profile.getPhone());
            preparedStatement.setString(4, profile.getEmail());
            preparedStatement.setString(5, profile.getAddress());
            preparedStatement.setString(6, profile.getCity());
            preparedStatement.setString(7, profile.getState());
            preparedStatement.setString(8, profile.getZip());
            preparedStatement.setInt(9, profile.getUserId());

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating profile", e);
            throw new RuntimeException("Failed to update profile", e);
        }

    }

    // Utility method to map ResultSet to Profile
    private Profile mapToProfile(ResultSet resultSet) throws SQLException {
        Profile profile = new Profile();
        profile.setUserId(resultSet.getInt("user_id"));
        profile.setFirstName(resultSet.getString("first_name"));
        profile.setLastName(resultSet.getString("last_name"));
        profile.setPhone(resultSet.getString("phone"));
        profile.setEmail(resultSet.getString("email"));
        profile.setAddress(resultSet.getString("address"));
        profile.setCity(resultSet.getString("city"));
        profile.setState(resultSet.getString("state"));
        profile.setZip(resultSet.getString("zip"));
        return profile;
    }
}
