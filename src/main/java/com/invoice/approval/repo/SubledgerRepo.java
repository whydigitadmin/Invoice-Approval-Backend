package com.invoice.approval.repo;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.invoice.approval.dto.SubledgerAlertDTO;

@Repository
public class SubledgerRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubledgerRepo.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<SubledgerAlertDTO> getSubledgersWithHighPercentage() {
        String sql = "select subledgercode, subledgername, ctrloffice, salesperson,\r\n"
        		+ "                                a.creditlimit, a.creditdays, totdue, percentage,a.category,b.tomail mailid,b.ccmail,\r\n"
        		+ "                                                                          CASE \r\n"
        		+ "        WHEN INSTR(SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1), '.') > 0 \r\n"
        		+ "        THEN INITCAP(\r\n"
        		+ "                SUBSTR(\r\n"
        		+ "                    SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1),\r\n"
        		+ "                    1,\r\n"
        		+ "                    INSTR(SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1), '.') - 1\r\n"
        		+ "                )\r\n"
        		+ "             )\r\n"
        		+ "        ELSE INITCAP(SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1))\r\n"
        		+ "    END as employee from vw_currentos a, gst_email b \r\n"
        		+ "                                where percentage > 80 and a.category not in ('Nominated') and a.subledgercode = b.partycode(+)\r\n"
        		+ "                                order by tomail,percentage desc ";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                SubledgerAlertDTO dto = new SubledgerAlertDTO();
                dto.setSubledgerCode(rs.getString("subledgercode"));
                dto.setSubledgerName(rs.getString("subledgername"));
                dto.setCategory(rs.getString("category")); // Map mailid
                dto.setCtrlOffice(rs.getString("ctrloffice"));
                dto.setSalesperson(rs.getString("salesperson"));
                dto.setCreditLimit(rs.getBigDecimal("creditlimit"));
                dto.setCreditDays(rs.getInt("creditdays"));
                dto.setTotdue(rs.getBigDecimal("totdue"));
                dto.setPercentage(rs.getBigDecimal("percentage"));
                dto.setMailid(rs.getString("mailid")); // Map mailid
                dto.setCcmail(rs.getString("ccmail")); // Map mailid
                dto.setEmployee(rs.getString("employee")); // Map mailid
                return dto;
            });
        } catch (Exception e) {
            LOGGER.error("Error fetching subledgers with high percentage: {}", e.getMessage());
            throw new RuntimeException("Database error while fetching subledger data", e);
        }
    }

    public List<SubledgerAlertDTO> getSubledgersBySalespersonEmail(String salespersonEmail) {
    	String sql = "select subledgercode, subledgername, ctrloffice, salesperson,\r\n"
        		+ "                                a.creditlimit, a.creditdays, totdue, percentage,a.category,b.tomail mailid,b.ccmail,\r\n"
        		+ "                                                  CASE \r\n"
        		+ "        WHEN INSTR(SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1), '.') > 0 \r\n"
        		+ "        THEN INITCAP(\r\n"
        		+ "                SUBSTR(\r\n"
        		+ "                    SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1),\r\n"
        		+ "                    1,\r\n"
        		+ "                    INSTR(SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1), '.') - 1\r\n"
        		+ "                )\r\n"
        		+ "             )\r\n"
        		+ "        ELSE INITCAP(SUBSTR(b.tomail, 1, INSTR(b.tomail, '@') - 1))\r\n"
        		+ "    END as employee from vw_currentos a, gst_email b \r\n"
        		+ "                                where percentage > 80 and a.category not in ('Nominated') and a.subledgercode = b.partycode(+)\r\n"
        		+ "                                order by tomail,percentage desc ";


        try {
            return jdbcTemplate.query(sql, new Object[]{salespersonEmail}, (rs, rowNum) -> {
                SubledgerAlertDTO dto = new SubledgerAlertDTO();
                dto.setSubledgerCode(rs.getString("subledgercode"));
                dto.setSubledgerName(rs.getString("subledgername"));
                dto.setCategory(rs.getString("category")); // Map mailid
                dto.setCtrlOffice(rs.getString("ctrloffice"));
                dto.setSalesperson(rs.getString("salesperson"));
                dto.setCreditLimit(rs.getBigDecimal("creditlimit"));
                dto.setCreditDays(rs.getInt("creditdays"));
                dto.setTotdue(rs.getBigDecimal("totdue"));
                dto.setPercentage(rs.getBigDecimal("percentage"));
//                dto.setCategory(rs.getString("category"));
                dto.setMailid(rs.getString("mailid"));
                dto.setCcmail(rs.getString("ccmail")); // Map mailid
                dto.setEmployee(rs.getString("employee"));
                return dto;
            });
        } catch (Exception e) {
            LOGGER.error("Error fetching subledgers for salesperson {}: {}", salespersonEmail, e.getMessage());
            throw new RuntimeException("Database error while fetching salesperson data", e);
        }
    }
}