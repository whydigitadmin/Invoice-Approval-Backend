package com.invoice.approval.repo;

import com.invoice.approval.dto.CurrentOutstandingDTO;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CurrentOutstandingRepo {

    @PersistenceContext
    private EntityManager entityManager;
    
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    public List<CurrentOutstandingDTO> getCurrentOutstandingDetails() {
        String sql = "SELECT subledgercode, subledgername, docid, docdate, refno, refdate, " +
                     "creditdays, creditlimit, ctrloffice, salesperson, totdue " +
                     "FROM VW_CURRENTOSDETAILS";
        
        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();
        List<CurrentOutstandingDTO> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            CurrentOutstandingDTO dto = new CurrentOutstandingDTO(
                row[0] != null ? row[0].toString() : null,
                row[1] != null ? row[1].toString() : null,
                row[2] != null ? row[2].toString() : null,
                parseDateString(row[3]),
                row[4] != null ? row[4].toString() : null,
                parseDateString(row[5]),
                row[6] != null ? ((Number) row[6]).intValue() : null,
                row[7] != null ? (BigDecimal) row[7] : null,
                row[8] != null ? row[8].toString() : null,
                row[9] != null ? row[9].toString() : null,
                row[10] != null ? (BigDecimal) row[10] : null
            );
            dtoList.add(dto);
        }
        
        return dtoList;
    }

    public List<CurrentOutstandingDTO> getCurrentOutstandingWithEmailDetails() {
        // Using spmail as the email address instead of GST_EMAILID
        String sql = "SELECT " +
                     "cos.subledgercode, " +
                     "cos.subledgername, " +
                     "cos.docid, " +
                     "cos.docdate, " +
                     "cos.refno, " +
                     "cos.refdate, " +
                     "cos.creditdays, " +
                     "cos.creditlimit, " +
                     "cos.ctrloffice, " +
                     "cos.salesperson, " +
                     "cos.totdue, " +
                     "ge.spmail as salespersonEmail , " +  // Use spmail as the email
                     "ge.CCMAIL as ccMail, " +
                     "ge.CATEGORY as category, " +
                     "ge.PARTYCODE as partyCode, " +
                     "ge.PARTYNAME as partyName, " +
                     "ge.SALESPERSONNAME as salespersonName, " +
                     "ge.SALESPERSONCODE as salespersonCode " +
                     "FROM VW_CURRENTOSDETAILS cos " +
                     "LEFT JOIN gst_email ge ON cos.subledgercode = ge.PARTYCODE " +
                     "WHERE  ge.spmail is not null";
        
        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();
        List<CurrentOutstandingDTO> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            CurrentOutstandingDTO dto = new CurrentOutstandingDTO(
                row[0] != null ? row[0].toString() : null,
                row[1] != null ? row[1].toString() : null,
                row[2] != null ? row[2].toString() : null,
                parseDateString(row[3]),
                row[4] != null ? row[4].toString() : null,
                parseDateString(row[5]),
                row[6] != null ? ((Number) row[6]).intValue() : null,
                row[7] != null ? (BigDecimal) row[7] : null,
                row[8] != null ? row[8].toString() : null,
                row[9] != null ? row[9].toString() : null,
                row[10] != null ? (BigDecimal) row[10] : null
            );
            
            // Set email details - spmail is at index 11
            if (row.length > 11) dto.setSalespersonEmail(row[11] != null ? row[11].toString() : null);
            if (row.length > 12) dto.setCcMail(row[12] != null ? row[12].toString() : null);
            if (row.length > 13) dto.setCategory(row[13] != null ? row[13].toString() : null);
            if (row.length > 14) dto.setPartyCode(row[14] != null ? row[14].toString() : null);
            if (row.length > 15) dto.setPartyName(row[15] != null ? row[15].toString() : null);
            if (row.length > 16) dto.setSalespersonName(row[16] != null ? row[16].toString() : null);
            if (row.length > 17) dto.setSalespersonCode(row[17] != null ? row[17].toString() : null);
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }
    
    public List<CurrentOutstandingDTO> getOutstandingBySalesperson(String salespersonEmail) {
        String sql = "SELECT " +
                     "cos.subledgercode, " +
                     "cos.subledgername, " +
                     "cos.docid, " +
                     "cos.docdate, " +
                     "cos.refno, " +
                     "cos.refdate, " +
                     "cos.creditdays, " +
                     "cos.creditlimit, " +
                     "cos.ctrloffice, " +
                     "cos.salesperson, " +
                     "cos.totdue, " +
                     "ge.spmail as salespersonEmail, " +  // Use spmail
                     "ge.CCMAIL as ccMail, " +
                     "ge.CATEGORY as category, " +
                     "ge.PARTYCODE as partyCode, " +
                     "ge.PARTYNAME as partyName, " +
                     "ge.SALESPERSONNAME as salespersonName, " +
                     "ge.SALESPERSONCODE as salespersonCode " +
                     "FROM VW_CURRENTOSDETAILS cos " +
                     "LEFT JOIN gst_email ge ON cos.subledgercode = ge.PARTYCODE " +
                     "WHERE ge.spmail = :salespersonEmail";
        
        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("salespersonEmail", salespersonEmail)
                .getResultList();
        
        List<CurrentOutstandingDTO> dtoList = new ArrayList<>();
        for (Object[] row : results) {
            CurrentOutstandingDTO dto = new CurrentOutstandingDTO(
                row[0] != null ? row[0].toString() : null,
                row[1] != null ? row[1].toString() : null,
                row[2] != null ? row[2].toString() : null,
                parseDateString(row[3]),
                row[4] != null ? row[4].toString() : null,
                parseDateString(row[5]),
                row[6] != null ? ((Number) row[6]).intValue() : null,
                row[7] != null ? (BigDecimal) row[7] : null,
                row[8] != null ? row[8].toString() : null,
                row[9] != null ? row[9].toString() : null,
                row[10] != null ? (BigDecimal) row[10] : null
            );
            
            if (row.length > 11) dto.setSalespersonEmail(row[11] != null ? row[11].toString() : null);
            if (row.length > 12) dto.setCcMail(row[12] != null ? row[12].toString() : null);
            if (row.length > 13) dto.setCategory(row[13] != null ? row[13].toString() : null);
            if (row.length > 14) dto.setPartyCode(row[14] != null ? row[14].toString() : null);
            if (row.length > 15) dto.setPartyName(row[15] != null ? row[15].toString() : null);
            if (row.length > 16) dto.setSalespersonName(row[16] != null ? row[16].toString() : null);
            if (row.length > 17) dto.setSalespersonCode(row[17] != null ? row[17].toString() : null);
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }
    
    // Helper method to parse date string in DD/MM/YYYY format
    private Date parseDateString(Object dateObj) {
        if (dateObj == null) return null;
        
        String dateStr = dateObj.toString().trim();
        if (dateStr.isEmpty()) return null;
        
        try {
            // Handle different possible formats
            if (dateStr.contains("/")) {
                return dateFormatter.parse(dateStr);
            } else {
                // Try alternative formats if needed
                SimpleDateFormat altFormatter = new SimpleDateFormat("yyyy-MM-dd");
                return altFormatter.parse(dateStr);
            }
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + dateStr + " - " + e.getMessage());
            return null;
        }
    }
}