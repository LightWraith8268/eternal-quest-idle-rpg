package com.eternalquest.data.dao

import androidx.room.*
import com.eternalquest.data.entities.BankItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BankDao {
    @Query("SELECT * FROM bank_items ORDER BY tabIndex, slotIndex")
    fun getAllBankItems(): Flow<List<BankItem>>
    
    @Query("SELECT * FROM bank_items WHERE tabIndex = :tabIndex ORDER BY slotIndex")
    fun getBankItemsForTab(tabIndex: Int): Flow<List<BankItem>>
    
    @Query("SELECT * FROM bank_items WHERE tabIndex = :tabIndex AND slotIndex = :slotIndex")
    suspend fun getBankItem(tabIndex: Int, slotIndex: Int): BankItem?
    
    @Query("SELECT * FROM bank_items WHERE itemId = :itemId LIMIT 1")
    suspend fun findBankItemById(itemId: String): BankItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankItem(bankItem: BankItem)
    
    @Update
    suspend fun updateBankItem(bankItem: BankItem)
    
    @Delete
    suspend fun deleteBankItem(bankItem: BankItem)
    
    @Query("DELETE FROM bank_items WHERE tabIndex = :tabIndex AND slotIndex = :slotIndex")
    suspend fun clearBankSlot(tabIndex: Int, slotIndex: Int)
    
    @Query("UPDATE bank_items SET quantity = quantity + :amount WHERE tabIndex = :tabIndex AND slotIndex = :slotIndex")
    suspend fun addToStack(tabIndex: Int, slotIndex: Int, amount: Int)
    
    @Query("SELECT COUNT(*) FROM bank_items WHERE tabIndex = :tabIndex")
    suspend fun getUsedSlotsInTab(tabIndex: Int): Int
    
    @Query("SELECT MAX(slotIndex) + 1 FROM bank_items WHERE tabIndex = :tabIndex")
    suspend fun getNextAvailableSlot(tabIndex: Int): Int?

    @Query("DELETE FROM bank_items")
    suspend fun clearAll()
}
