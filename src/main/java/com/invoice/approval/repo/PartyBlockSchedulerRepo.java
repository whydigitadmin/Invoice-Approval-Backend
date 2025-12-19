package com.invoice.approval.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.PartyMasterVO;


@Repository
public interface PartyBlockSchedulerRepo  extends JpaRepository<PartyMasterVO, Long> {

    // 1. CREDIT - UNBLOCK PARTIES WITH PERCENT <= 120
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 0  \r\n"
    		+ " where ccod = 'Credit' and block = 2  and active = 'T' and party_code not in (select subledgercode from (SELECT \r\n"
    		+ "    subledgername,\r\n"
    		+ "    subledgercode,\r\n"
    		+ "    creditlimit,salesperson,category,ctrloffice,\r\n"
    		+ "    totdue,\r\n"
    		+ "    CASE \r\n"
    		+ "        WHEN creditlimit > 0 THEN ROUND((totdue / creditlimit) * 100, 2)\r\n"
    		+ "        ELSE 0\r\n"
    		+ "    END AS percent\r\n"
    		+ "FROM vw_currentos) where percent > 120 )"
    		, nativeQuery = true)
    void unblockCreditParties();

    // 2. CREDIT - BLOCK PARTIES WITH PERCENT > 120
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 2 where party_code in (\r\n"
    		+ "select subledgercode from (SELECT \r\n"
    		+ "    subledgername,\r\n"
    		+ "    subledgercode,\r\n"
    		+ "    creditlimit,salesperson,category,ctrloffice,\r\n"
    		+ "    totdue,\r\n"
    		+ "    CASE \r\n"
    		+ "        WHEN creditlimit > 0 THEN ROUND((totdue / creditlimit) * 100, 2)\r\n"
    		+ "        ELSE 0\r\n"
    		+ "    END AS percent\r\n"
    		+ "FROM vw_currentos) where percent > 120 and subledgercode not in \r\n"
    		+ "(select party_code from mg_partyhdr where block = 2)\r\n"
    		+ ")\r\n"
    		+ "", nativeQuery = true)
    void blockCreditParties();

    // 3. SUBLEDGER - BLOCK CREDIT WITH PERCENT > 120
    @Modifying
    @Transactional
    @Query(value = "update mg_subledger set block = 2 where subledgercode in (\r\n"
    		+ "select subledgercode from (SELECT \r\n"
    		+ "    subledgername,\r\n"
    		+ "    subledgercode,\r\n"
    		+ "    creditlimit,salesperson,category,ctrloffice,\r\n"
    		+ "    totdue,\r\n"
    		+ "    CASE \r\n"
    		+ "        WHEN creditlimit > 0 THEN ROUND((totdue / creditlimit) * 100, 2)\r\n"
    		+ "        ELSE 0\r\n"
    		+ "    END AS percent\r\n"
    		+ "FROM vw_currentos) where percent > 120 and subledgercode not in \r\n"
    		+ "(select subledgercode from mg_subledger where block = 2)\r\n"
    		+ ")", nativeQuery = true)
    void blockCreditSubledger();

    // 4. UNBLOCK PARTIES WITH PERCENT < 120
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 0 where party_code in (\r\n"
    		+ "select subledgercode from (SELECT \r\n"
    		+ "    subledgername,\r\n"
    		+ "    subledgercode,\r\n"
    		+ "    creditlimit,salesperson,category,ctrloffice,\r\n"
    		+ "    totdue,\r\n"
    		+ "    CASE \r\n"
    		+ "        WHEN creditlimit > 0 THEN ROUND((totdue / creditlimit) * 100, 2)\r\n"
    		+ "        ELSE 0\r\n"
    		+ "    END AS percent\r\n"
    		+ "FROM vw_currentos) where percent < 120 and subledgercode in \r\n"
    		+ "(select party_code from mg_partyhdr where block = 2)\r\n"
    		+ ")", nativeQuery = true)
    void unblockPartiesWithLowPercent();

    // 5. UNBLOCK SUBLEDGER WITH PERCENT < 120
    @Modifying
    @Transactional
    @Query(value = "update mg_subledger set block = 0 where subledgercode in (\r\n"
    		+ "select subledgercode from (SELECT \r\n"
    		+ "    subledgername,\r\n"
    		+ "    subledgercode,\r\n"
    		+ "    creditlimit,salesperson,category,ctrloffice,\r\n"
    		+ "    totdue,\r\n"
    		+ "    CASE \r\n"
    		+ "        WHEN creditlimit > 0 THEN ROUND((totdue / creditlimit) * 100, 2)\r\n"
    		+ "        ELSE 0\r\n"
    		+ "    END AS percent\r\n"
    		+ "FROM vw_currentos) where percent < 120 and subledgercode  in \r\n"
    		+ "(select subledgercode from mg_subledger where block = 2 )\r\n"
    		+ ")", nativeQuery = true)
    void unblockSubledgerWithLowPercent();

    // 6. UNBLOCK ALL COD PARTIES (INITIAL)
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 0  where ccod = 'COD' and block = 2 and active = 'T' and category <> 'Nominated'", nativeQuery = true)
    void unblockAllCODParties();

    // 7. BLOCK COD PARTIES WITH OVERDUE
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 2 where party_code in \r\n"
    		+ "(select distinct subledgercode from VW_CURRENTCODos where subledgercode in \r\n"
    		+ "(select party_code from mg_partyhdr where ccod = 'COD' and active = 'T' and category <> 'Nominated' and block = 0)\r\n"
    		+ "and totdue > 1000 and days > 7 ) and ccod = 'COD' and active = 'T' and category <> 'Nominated'", nativeQuery = true)
    void blockOverdueCODParties();

    // 8. BLOCK SUBLEDGER FOR COD PARTIES
    @Modifying
    @Transactional
    @Query(value = "update mg_subledger set block = 2 where subledgercode in (\r\n"
    		+ "select party_code from mg_partyhdr where ccod = 'COD' and block = 2)  and block <> 2", nativeQuery = true)
    void blockCODSubledger();

    // 9. UNBLOCK PAID COD PARTIES
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 0 where party_code in (\r\n"
    		+ "select subledgercode from (SELECT \r\n"
    		+ "    subledgername,\r\n"
    		+ "    subledgercode,\r\n"
    		+ "    creditlimit,salesperson,category,ctrloffice,\r\n"
    		+ "    totdue,\r\n"
    		+ "    CASE \r\n"
    		+ "        WHEN creditlimit > 0 THEN ROUND((totdue / creditlimit) * 100, 2)\r\n"
    		+ "        ELSE 0\r\n"
    		+ "    END AS percent,block\r\n"
    		+ "FROM VW_CURRENTCODOS) where  category <> 'Nominated' and totdue <= 0 and block = 'Yes'  and subledgercode not in \r\n"
    		+ "(select party_code from mg_partyhdr where block = 1)\r\n"
    		+ "and subledgercode in (select party_code from mg_partyhdr where block = 2 and category <> 'Nominated')\r\n"
    		+ ")", nativeQuery = true)
    void unblockPaidCODParties();

    // 10. UNBLOCK NA PARTIES
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 0  where ccod = 'NA' and active = 'T'  and block not in (0,1) ", nativeQuery = true)
    void unblockNAParties();

    // 11. UNBLOCK NA SUBLEDGER
    @Modifying
    @Transactional
    @Query(value = "update mg_subledger set block = 0  where subledgercode in ( select party_code from mg_partyhdr where ccod = 'NA' and active = 'T' and block not in (0,1)) and \r\n"
    		+ "block <> 0 ", nativeQuery = true)
    void unblockNASubledger();

    // 12. UNBLOCK ALL FX PARTIES
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 0  where party_type = 'FX' and active = 'T'", nativeQuery = true)
    void unblockAllFXParties();

    // 13. BLOCK OVERDUE FX PARTIES
    @Modifying
    @Transactional
    @Query(value = "update mg_partyhdr set block = 2 where party_code in \r\n"
    		+ "(select subledgercode from VW_CURRENTFXOS where days > 60 and totdue < -100 and ccod = 'FX')", nativeQuery = true)
    void blockOverdueFXParties();

    // 14. UNBLOCK ALL FX SUBLEDGER
    @Modifying
    @Transactional
    @Query(value = "update mg_subledger set block = 0  where  subledgertype = 'FX' and active = 'T' ", nativeQuery = true)
    void unblockAllFXSubledger();

    // 15. BLOCK OVERDUE FX SUBLEDGER
    @Modifying
    @Transactional
    @Query(value = "update mg_subledger set block = 2 where subledgercode in \r\n"
    		+ "(select subledgercode from VW_CURRENTFXOS where days > 60 and totdue < -100 and ccod = 'FX' )", nativeQuery = true)
    void blockOverdueFXSubledger();

    // 16. UNBLOCK SUBLEDGER FOR UNBLOCKED COD PARTIES
    @Modifying
    @Transactional
    @Query(value = "update mg_subledger set block = 0 where subledgercode in (\r\n"
    		+ "select party_code from mg_partyhdr where ccod = 'COD' and category <> 'Nominated' and block = 0) and block = 2", nativeQuery = true)
    void unblockSubledgerForCOD();
}