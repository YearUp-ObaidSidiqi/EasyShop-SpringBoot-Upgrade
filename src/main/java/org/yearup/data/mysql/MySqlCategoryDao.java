package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    @Autowired
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();

        String que = """
               SELECT * FROM easyshop.categories;""";

        try(Connection connection = getConnection();
            PreparedStatement query = connection.prepareStatement(que);
            ResultSet results = query.executeQuery())
        {
            while (results.next()){
                categories.add(mapRow(results));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {

        Category category;

        String que = """
                SELECT
                *
                FROM categories
                WHERE category_id = ?;""";

        try(Connection connection = getConnection();
            PreparedStatement query = connection.prepareStatement(que);
        )
        {
            query.setInt(1, categoryId);
            try( ResultSet results = query.executeQuery()){
                while (results.next()){
                    return mapRow(results);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Category create(Category category) {
        String que = """
            INSERT INTO categories (name, description)
            VALUES (?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement query = connection.prepareStatement(que, PreparedStatement.RETURN_GENERATED_KEYS)) {

            query.setString(1, category.getName());
            query.setString(2, category.getDescription());

            query.executeUpdate();

            try (ResultSet generatedKeys = query.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating category", e);
        }
        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {

        String que = """
                UPDATE categories
                SET name = ?, description = ?
                WHERE category_id = ?
                """;

        try(Connection connection = getConnection();
            PreparedStatement query = connection.prepareStatement(que);
        )
        {
            query.setString(1, category.getName());
            query.setString(2, category.getDescription());
            query.setInt(3, categoryId);

            query.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {

        String que = """
                DELETE FROM categories
                WHERE category_id = ?
                """;

        try(Connection connection = getConnection();
            PreparedStatement query = connection.prepareStatement(que);
        )
        {
            query.setInt(1, categoryId);
            query.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}