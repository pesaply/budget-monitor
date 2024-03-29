package fr.alvini.insta.budgetmonitor.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import fr.alvini.insta.budgetmonitor.bdd.Database;
import fr.alvini.insta.budgetmonitor.model.Budget;
import fr.alvini.insta.budgetmonitor.model.Category;
import fr.alvini.insta.budgetmonitor.model.ObjectModel;
import fr.alvini.insta.budgetmonitor.model.Operation;
import fr.alvini.insta.budgetmonitor.model.Recurrence;

public class OperationDAO extends DAOBase {
	private Context context;

	public OperationDAO(Context pContext) {
		super(pContext);
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 * @param operation
	 * Add an operation
	 */
	public void ajouter(Operation operation) {
		super.open();
		ContentValues values = new ContentValues();
		String date_added = ObjectModel.formatDate(operation.getDate_added(), true);
		values.put(Database.OPERATION_DESCRIPTION, operation.getDescription());
		values.put(Database.OPERATION_TYPE, operation.getType());
		values.put(Database.OPERATION_AMOUNT, operation.getAmount());
		values.put(Database.OPERATION_ADD_DATE, date_added);
		values.put(Database.OPERATION_BUDGET, operation.getBudget().getId_budget());
//		System.out.println("Debug 4 : "+operation.getCategory().getId_category());
		if (operation.getCategory() != null)
			values.put(Database.OPERATION_CATEGORY, operation.getCategory().getId_category());
		else
			values.put(Database.OPERATION_CATEGORY, 0);
		if (operation.getRecurrence() != null)
			values.put(Database.OPERATION_RECURRENCE, operation.getRecurrence().getId_recurrence());
		else
			values.put(Database.OPERATION_RECURRENCE, 0);
		values.put(Database.OPERATION_RECURRENCE_STATUS, 0);
		mDb.insert(Database.OPERATION_TABLE_NAME, null, values);
		super.close();
	}

	/**
	 * @param id
	 * Delete the operation from DB with its id
	 */
	public void supprimer(long id) {
		super.open();
		mDb.delete(Database.OPERATION_TABLE_NAME, Database.OPERATION_KEY + " = ? ", new String[] {String.valueOf(id)});
		super.close();
	}

	/**
	 * @param operation
	 * Update an operation
	 */
	public void modifier(Operation operation) {
		super.open();
		ContentValues values = new ContentValues();
		String date_added = ObjectModel.formatDate(operation.getDate_added(), true);
		values.put(Database.OPERATION_DESCRIPTION, operation.getDescription());
		values.put(Database.OPERATION_TYPE, operation.getType());
		values.put(Database.OPERATION_AMOUNT, operation.getAmount());
		values.put(Database.OPERATION_ADD_DATE, date_added);
		values.put(Database.OPERATION_BUDGET, operation.getBudget().getId_budget());
		if (operation.getCategory() != null)
			values.put(Database.OPERATION_CATEGORY, operation.getCategory().getId_category());
		else
			values.put(Database.OPERATION_CATEGORY, 0);
		if (operation.getRecurrence() != null)
			values.put(Database.OPERATION_RECURRENCE, operation.getRecurrence().getId_recurrence());
		else
			values.put(Database.OPERATION_RECURRENCE, 0);
		values.put(Database.OPERATION_RECURRENCE_STATUS, 0);
		mDb.update(Database.OPERATION_TABLE_NAME, values, Database.OPERATION_KEY + " = ? ", new String[] {String.valueOf(operation.getId_operation())});
		super.close();
	}

	/**
	 * @param id
	 * Get an operation with its id
	 * @throws ParseException 
	 */
	public Operation selectionner(long id) throws ParseException {
		super.open();
		String sql = "SELECT "+Database.OPERATION_KEY+" as _id, "
						+Database.OPERATION_DESCRIPTION+ ", "
						+Database.OPERATION_TYPE+", "
						+Database.OPERATION_AMOUNT+ ", "
						+Database.OPERATION_ADD_DATE+ ", "
						+Database.OPERATION_BUDGET+ ", "
						+Database.OPERATION_CATEGORY+ ", "
						+Database.OPERATION_RECURRENCE+ ", "
						+Database.OPERATION_RECURRENCE_STATUS+
					 " FROM "+Database.OPERATION_TABLE_NAME+
					 " WHERE "+Database.OPERATION_KEY+ " = ?";
		Cursor cursor = mDb.rawQuery(sql, new String[] {String.valueOf(id)});
		Operation operation = null;
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
			long id_operation = cursor.getLong(0);

			String description = cursor.getString(1);
			String type = cursor.getString(2);
			double amount = cursor.getDouble(3);
			
			String date_added_str = cursor.getString(4);
			GregorianCalendar date_added = new GregorianCalendar(Integer.valueOf(ObjectModel.getDateElement("year", date_added_str)), Integer.valueOf(ObjectModel.getDateElement("month", date_added_str)), Integer.valueOf(ObjectModel.getDateElement("day", date_added_str)));
			
			
			Budget budget = new Budget();
			if (cursor.getLong(5) != 0) {
//				BudgetDAO budDAO = new BudgetDAO(this.getContext());
				budget.setId_budget(cursor.getLong(5));
			}
			
			Category category = new Category();
			if (cursor.getLong(6) != 0) {
//				CategoryDAO catDAO = new CategoryDAO(this.getContext());
				category.setId_category(cursor.getLong(6));
			}
			
			Recurrence recurrence = new Recurrence();
			if (cursor.getInt(7) != 0) {
//				RecurrenceDAO recDAO = new RecurrenceDAO(this.getContext());
				recurrence.setId_recurrence(cursor.getLong(7));
			}
			Integer rec_status = cursor.getInt(8);
			operation = new Operation(id_operation, budget, category, amount, description, type, date_added, recurrence, rec_status);
			//System.out.println("Results are : "+metier.getId()+" / "+metier.getIntitule()+" / "+metier.getSalaire()+".");
		}
		super.close();
		return operation;
	}
	
	/**
	 * @param id
	 * Gets all operations
	 * @throws ParseException 
	 */
	public List<Operation> selectionnerAll() throws ParseException {
		List<Operation> list = new ArrayList<Operation>();
		int id_budget = -1;
		long id_category = -1;
		long id_recurrence = -1;
		super.open();
		String sql = "SELECT "+Database.OPERATION_KEY+" as _id, "
						+Database.OPERATION_DESCRIPTION+ ", "
						+Database.OPERATION_TYPE+", "
						+Database.OPERATION_AMOUNT+ ", "
						+Database.OPERATION_ADD_DATE+ ", "
						+Database.OPERATION_BUDGET+ ", "
						+Database.OPERATION_CATEGORY+ ", "
						+Database.OPERATION_RECURRENCE+ ", "
						+Database.OPERATION_RECURRENCE_STATUS+
					 " FROM "+Database.OPERATION_TABLE_NAME+"";
		Cursor cursor = mDb.rawQuery(sql, new String[] {});
		Operation operation = null;
		//Toast.makeText(pContext, cursor.getCount(), Toast.LENGTH_LONG).show();
		if (cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				long id_operation = cursor.getLong(0);

				String description = cursor.getString(1);
				String type = cursor.getString(2);
				double amount = cursor.getDouble(3);
				
				String date_added_str = cursor.getString(4);
//				System.out.println(date_added_str);
				GregorianCalendar date_added = new GregorianCalendar(Integer.valueOf(ObjectModel.getDateElement("year", date_added_str)), Integer.valueOf(ObjectModel.getDateElement("month", date_added_str)), Integer.valueOf(ObjectModel.getDateElement("day", date_added_str)));
				
				Budget budget = new Budget();
				if (cursor.getLong(5) != 0) {
//					BudgetDAO budDAO = new BudgetDAO(this.getContext());
					budget.setId_budget(cursor.getLong(5));
				}
				
				Category category = new Category();
				if (cursor.getLong(6) != 0) {
//					CategoryDAO catDAO = new CategoryDAO(this.getContext());
					category.setId_category(cursor.getLong(6));
				}
				
				Recurrence recurrence = new Recurrence();
				if (cursor.getInt(7) != 0) {
//					RecurrenceDAO recDAO = new RecurrenceDAO(this.getContext());
					recurrence.setId_recurrence(cursor.getLong(7));
				}
				Integer rec_status = cursor.getInt(8);
				operation = new Operation(id_operation, budget, category, amount, description, type, date_added, recurrence, rec_status);
				
				list.add(operation);
			}
		}
		super.close();
		return list;
	}
	
	/**
	 * @param id
	 * Get all operations for a budget
	 * @throws ParseException 
	 */
	public List<Operation> selectionnerParBudget(long id) throws ParseException {
		List<Operation> list = new ArrayList<Operation>();
		super.open();
		String sql = "SELECT "+Database.OPERATION_KEY+" as _id, "
						+Database.OPERATION_DESCRIPTION+ ", "
						+Database.OPERATION_TYPE+", "
						+Database.OPERATION_AMOUNT+ ", "
						+Database.OPERATION_ADD_DATE+ ", "
						+Database.OPERATION_BUDGET+ ", "
						+Database.OPERATION_CATEGORY+ ", "
						+Database.OPERATION_RECURRENCE+ ", "
						+Database.OPERATION_RECURRENCE_STATUS+
					 " FROM "+Database.OPERATION_TABLE_NAME+
					 " WHERE "+Database.OPERATION_BUDGET+ " = ?";
		Cursor cursor = mDb.rawQuery(sql, new String[] {String.valueOf(id)});
		Operation operation = null;
		if (cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				long id_operation = cursor.getLong(0);

				String description = cursor.getString(1);
				String type = cursor.getString(2);
				double amount = cursor.getDouble(3);
				
				String date_added_str = cursor.getString(4);
//				System.out.println(date_added_str);
				GregorianCalendar date_added = new GregorianCalendar(Integer.valueOf(ObjectModel.getDateElement("year", date_added_str)), Integer.valueOf(ObjectModel.getDateElement("month", date_added_str)), Integer.valueOf(ObjectModel.getDateElement("day", date_added_str)));
				
				Budget budget = new Budget();
				if (cursor.getLong(5) != 0) {
//					BudgetDAO budDAO = new BudgetDAO(this.getContext());
					budget.setId_budget(cursor.getLong(5));
				}
				
				Category category = new Category();
//				System.out.println("Id categorie dans classe OpeDAO : "+cursor.getLong(6));
				if (cursor.getLong(6) != 0) {
//					CategoryDAO catDAO = new CategoryDAO(this.getContext());
					category.setId_category(cursor.getLong(6));
				}
				
				Recurrence recurrence = new Recurrence();
				if (cursor.getInt(7) != 0) {
//					RecurrenceDAO recDAO = new RecurrenceDAO(this.getContext());
					recurrence.setId_recurrence(cursor.getLong(7));
				}
				Integer rec_status = cursor.getInt(8);
				operation = new Operation(id_operation, budget, category, amount, description, type, date_added, recurrence, rec_status);
				
				list.add(operation);
			}
		}
		super.close();
		return list;
	}
}
