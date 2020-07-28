package org.dell.kube.pages;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;


public class MySqlPageRepositoryTest {
    private IPageRepository repo;
    private JdbcTemplate jdbcTemplate;

    @Before
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
    @BeforeEach
    public void setUp() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_USERNAME"));
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_PASSWORD"));
        repo = new MySqlPageRepository(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM pages");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void createInsertsAPageRecord() {
        Page newPage = new Page("XYZ", "Bangalore", 123, "1234567890");
        Page page = repo.create(newPage);

        Map<String, Object> foundPage = jdbcTemplate.queryForMap("Select * from pages where id = ?", page.getId());

        assertThat(foundPage.get("id")).isEqualTo(page.getId());
        assertThat(foundPage.get("business_name")).isEqualTo("XYZ");
        assertThat(foundPage.get("address")).isEqualTo("Bangalore");
        assertThat(foundPage.get("category_id")).isEqualTo(123L);
        assertThat(foundPage.get("contact_number")).isEqualTo("1234567890");
    }

    @Test
    public void createReturnsTheCreatedPage() {
        Page newPage = new Page("XYZ", "Bangalore", 123, "1234567890");
        Page page = repo.create(newPage);

        assertThat(page.getId()).isNotNull();
        assertThat(page.getBusinessName()).isEqualTo("XYZ");
        assertThat(page.getAddress()).isEqualTo("Bangalore");
        assertThat(page.getCategoryId()).isEqualTo(123);
        assertThat(page.getContactNumber()).isEqualTo("1234567890");
    }

    @Test
    public void findFindsAPage() {
        jdbcTemplate.execute(
                "INSERT INTO pages (id, business_name, address, category_id, contact_number) " +
                        "VALUES (321,\"XYZ\", \"Bangalore\", 123, \"1234567890\")"
        );

        Page page = repo.read(321L);

        assertThat(page.getId()).isEqualTo(321L);
        assertThat(page.getBusinessName()).isEqualTo("XYZ");
        assertThat(page.getAddress()).isEqualTo("Bangalore");
        assertThat(page.getCategoryId()).isEqualTo(123);
        assertThat(page.getContactNumber()).isEqualTo("1234567890");
    }

    @Test
    public void findReturnsNullWhenNotFound() {
        Page page = repo.read(321L);

        assertThat(page).isNull();
    }

    @Test
    public void listFindsAllTimeEntries() {
        jdbcTemplate.execute(
                "INSERT INTO pages (id, business_name, address, category_id, contact_number) " +
                        "VALUES (321,\"XYZ\", \"Bangalore\", 123, \"1234567890\")"
        );

        List<Page> pages = repo.list();
        assertThat(pages.size()).isEqualTo(1);

        Page page = pages.get(0);
        assertThat(page.getId()).isEqualTo(321L);
        assertThat(page.getBusinessName()).isEqualTo("XYZ");
        assertThat(page.getAddress()).isEqualTo("Bangalore");
        assertThat(page.getCategoryId()).isEqualTo(123);
        assertThat(page.getContactNumber()).isEqualTo("1234567890");
    }

    @Test
    public void updateReturnsTheUpdatedRecord() {
        jdbcTemplate.execute(
                "INSERT INTO pages (id, business_name, address, category_id, contact_number) " +
                        "VALUES (1000, \"ABC\" , \"Bangalore\", 321, \"1876543210\")");

        Page pageUpdates = new Page("ABC" , "Bangalore", 321, "987654321");

        Page updatedPage = repo.update(pageUpdates,1000L);

        assertThat(updatedPage.getId()).isEqualTo(1000L);
        assertThat(updatedPage.getBusinessName()).isEqualTo("ABC");
        assertThat(updatedPage.getAddress()).isEqualTo("Bangalore");
        assertThat(updatedPage.getCategoryId()).isEqualTo(321);
        assertThat(updatedPage.getContactNumber()).isEqualTo("987654321");
    }

    @Test
    public void updateUpdatesTheRecord() {
        jdbcTemplate.execute(
                "INSERT INTO pages (id, business_name, address, category_id, contact_number) " +
                        "VALUES (1000, \"ABC\" , \"Bangalore\", 321, \"1876543210\")");

        Page updatedPage = new Page("ABC" , "Bangalore", 321, "987654321");

        Page page = repo.update(updatedPage,1000L);

        Map<String, Object> foundPage = jdbcTemplate.queryForMap("Select * from pages where id = ?", page.getId());

        assertThat(foundPage.get("id")).isEqualTo(page.getId());
        assertThat(updatedPage.getBusinessName()).isEqualTo("ABC");
        assertThat(updatedPage.getAddress()).isEqualTo("Bangalore");
        assertThat(updatedPage.getCategoryId()).isEqualTo(321);
        assertThat(updatedPage.getContactNumber()).isEqualTo("987654321");
    }

    @Test
    public void deleteRemovesTheRecord() {
        jdbcTemplate.execute(
                "INSERT INTO pages (id, business_name, address, category_id, contact_number) " +
                        "VALUES (1000, \"ABC\" , \"Bangalore\", 321, \"1876543210\")");

        repo.delete(1000L);

        Map<String, Object> notFoundPage = jdbcTemplate.queryForMap("Select count(*) count from pages where id = ?", 1000L);
        assertThat(notFoundPage.get("count")).isEqualTo(0L);

    }
}