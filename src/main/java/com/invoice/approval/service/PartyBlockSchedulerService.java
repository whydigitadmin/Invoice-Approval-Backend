package com.invoice.approval.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoice.approval.repo.PartyBlockSchedulerRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PartyBlockSchedulerService {

    @Autowired
    private PartyBlockSchedulerRepo repo;

    /**
     * Runs every 5 minutes
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void updatePartyBlockStatus() {

        log.info("🔁 Party block scheduler started");

        try {
            // Credit Related Operations
            log.info("Processing Credit Parties...");
            repo.unblockCreditParties();               // 1. Unblock credit parties with percent <= 120
            repo.unblockSubledgerWithLowPercent();     // 5. Unblock subledger with percent < 120
            repo.unblockPartiesWithLowPercent();       // 4. Unblock parties with percent < 120
            repo.blockCreditParties();                 // 2. Block credit parties with percent > 120
            repo.blockCreditSubledger();               // 3. Block subledger for credit parties
            
            
            
            // COD Related Operations
            log.info("Processing COD Parties...");
            repo.unblockAllCODParties();               // 6. Unblock all COD parties initially
            repo.unblockPaidCODParties();              // 9. Unblock paid COD parties
            repo.blockOverdueCODParties();             // 7. Block COD parties with overdue
            repo.blockCODSubledger();                  // 8. Block subledger for COD parties
            
            
            // NA Related Operations
            log.info("Processing NA Parties...");
            repo.unblockNAParties();                   // 10. Unblock NA parties
            repo.unblockNASubledger();                 // 11. Unblock NA subledger
            
            // FX Related Operations
            log.info("Processing FX Parties...");
            repo.unblockAllFXParties();                // 12. Unblock all FX parties
            repo.blockOverdueFXParties();              // 13. Block overdue FX parties
            repo.unblockAllFXSubledger();              // 14. Unblock all FX subledger
            repo.blockOverdueFXSubledger();            // 15. Block overdue FX subledger
            
            // Final cleanup for COD subledger
            log.info("Final cleanup...");
            repo.unblockSubledgerForCOD();             // 16. Unblock subledger for unblocked COD parties

            log.info("✅ Party block scheduler completed successfully");

        } catch (Exception e) {
            log.error("❌ Error in Party block scheduler", e);
            throw e; // Re-throw to trigger transaction rollback if needed
        }
    }
}