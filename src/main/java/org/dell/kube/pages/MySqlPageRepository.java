package org.dell.kube.pages;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class MySqlPageRepository implements IPageRepository {
    private final JdbcTemplate jdbcTemplate;
    public MySqlPageRepository(DataSource dataSource)
    {
        this.jdbcTemplate=new JdbcTemplate(dataSource);
        this.init();
    }
    @Override
    public Page create(Page page) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO pages (business_name, address, category_id, contact_number) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );
            statement.setString(1, page.getBusinessName());
            statement.setString(2, page.getAddress());
            statement.setLong(3,page.getCategoryId());
            statement.setString(4,page.getContactNumber());

            return statement;
        }, generatedKeyHolder);

        return read(generatedKeyHolder.getKey().longValue());

    }
    @Override
    public Page read(long id) {
        return jdbcTemplate.query(
                "SELECT id, business_name, address, category_id, contact_number FROM pages WHERE id = ?",
                new Object[]{id},
                extractor);
    }

    @Override
    public List<Page> list() {
        return jdbcTemplate.query("SELECT id, business_name, address, category_id, contact_number FROM pages", mapper);
    }

    @Override
    public Page update(Page page, long id) {
        jdbcTemplate.update("UPDATE pages " +
                        "SET business_name = ?, address = ?, category_id = ?,  contact_number = ? " +
                        "WHERE id = ?",
                page.getBusinessName(),
                page.getAddress(),
                page.getCategoryId(),
                page.getContactNumber(),
                id);

        return read(id);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM pages WHERE id = ?", id);
    }

    private final RowMapper<Page> mapper = (rs, rowNum) -> new Page(
            rs.getLong("id"),
            rs.getString("business_name"),
            rs.getString("address"),
            rs.getLong("category_id"),
            rs.getString("contact_number")
    );
    private final ResultSetExtractor<Page> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;

    private void init(){
        jdbcTemplate.execute("create table if not exists pages(\n" +
                "  id bigint(20) not null auto_increment,\n" +
                "  business_name VARCHAR(50),\n" +
                "  address VARCHAR(50),\n" +
                "  category_id bigint(20),\n" +
                "  contact_number VARCHAR(50),\n" +
                "\n" +
                "  primary key (id)\n" +
                ")\n" +
                "engine = innodb\n" +
                "default charset = utf8;");
    }
}