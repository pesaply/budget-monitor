package fr.alvini.insta.budgetmonitor.activities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;
import fr.alvini.insta.budgetmonitor.HomeActivity;
import fr.alvini.insta.budgetmonitor.R;
import fr.alvini.insta.budgetmonitor.dao.BudgetDAO;
import fr.alvini.insta.budgetmonitor.dao.CategoryDAO;
import fr.alvini.insta.budgetmonitor.dao.OperationDAO;
import fr.alvini.insta.budgetmonitor.dao.RecurrenceDAO;
import fr.alvini.insta.budgetmonitor.model.Budget;
import fr.alvini.insta.budgetmonitor.model.Category;
import fr.alvini.insta.budgetmonitor.model.Operation;
import fr.alvini.insta.budgetmonitor.model.Recurrence;

public class ModifierOperation extends Activity implements OnClickListener{
	
	private String choixOperation;
	//liste déroulante concernant l'opération 
	static final String[] operation = new String[]{
		"Dépense", "Revenu"};

	private String choixCategorieUt;
	private long choixCategorieId = 0;
	private Category category = null;
	private List<Category> categories = null;
	private CategoryDAO catDAO = null;
	private int positionCat = -1;
	//liste déroulante concernant l'opération 
	static final String[] categorie = new String[]{
		"Par défaut","Alimentation", "Bar", "Courses", "Divers", "Epargne",  "Impots", "Logement/Charges", "Salaire", "Transport"};

	private EditText montant;
	private EditText libelle;
	private String recurrence;
	private List<Recurrence> recurrents = null;
	private RecurrenceDAO recDAO = null;
	private int positionRec = -1;
	//liste déroulante concernant la récurrence 
	static final String[] recurrent = new String[]{
		"Aucune", "Quotidiennement", "Hebdomadairement", "Mensuel(le)", "Annuelle"};
	private Button modifierOperation;
	private Button show_popup;
	private GridLayout container;
	private Button supprimerOperation;

	//déclaration popup
	private Dialog custom;
	private Button addbtn;
	private Button cancel;
	private EditText ajoutCateg;
	private String newCateg;
	
	private ArrayAdapter<String> adapterCategorie;
	private Spinner choixCat;
	private Spinner choix;
	private List<String> categoriesString = null;
	private Budget getBudget = null;
	private BudgetDAO budDao = null;
	private Operation getOperation = null;
	private OperationDAO operationDao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modifier_operation);
		//this.container = (GridLayout)findViewById(R.id.gridLayout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intentPassed = getIntent();
		long id_operation = intentPassed.getLongExtra("id_operation", -1);
//		Toast.makeText(ModifierOperation.this, String.valueOf(id_operation), Toast.LENGTH_LONG).show();
		operationDao = new OperationDAO(ModifierOperation.this);
		if (id_operation != -1) {
			try {
				getOperation = operationDao.selectionner(id_operation);
				System.out.println(getOperation.getId_operation());
				System.out.println("Id cat getOperation 1ere fois "+getOperation.getCategory().getId_category());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			//getOperation = new Operation();
		}
		
		//correspondance entre les objets et les widgets
				this.montant = (EditText)findViewById(R.id.montant);
				this.libelle = (EditText)findViewById(R.id.libelle);
				this.modifierOperation = (Button)findViewById(R.id.modifierOperation);
				this.show_popup = (Button)findViewById(R.id.show_popup);
				this.supprimerOperation = (Button)findViewById(R.id.supprimerOperation);

				this.montant.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });

				//rendre le bouton écoutable
				this.modifierOperation.setOnClickListener(this);
				this.show_popup.setOnClickListener(this);
				this.supprimerOperation.setOnClickListener(this);
			
				recDAO = new RecurrenceDAO(ModifierOperation.this);
		        recurrents = recDAO.selectionnerAll();
		        final List<Long> recurrentsIds = new ArrayList<Long>();
		        List<String> recurrentsString = new ArrayList<String>();
//		        int position = -1;
				int i = 0;
				for (Recurrence rec : recurrents) {
					recurrentsString.add(rec.getDescription());
					recurrentsIds.add(rec.getId_recurrence());
					if (rec.getId_recurrence() == getOperation.getRecurrence().getId_recurrence())
						positionRec = i;
					i++;
				}
				
		        catDAO = new CategoryDAO(ModifierOperation.this);
		        categories = catDAO.selectionnerAll();
		        categoriesString = new ArrayList<String>();
		        final List<Long> categoriesIds = new ArrayList<Long>();
		        i = 0;
		        for(Category cat : categories) {
		        	categoriesString.add(cat.getDescription());
		        	categoriesIds.add(cat.getId_category());
		        	System.out.println("Id cat de la liste "+cat.getId_category());
		        	System.out.println("Id cat getOperation "+getOperation.getCategory().getId_category());
					if (cat.getId_category() == getOperation.getCategory().getId_category())
						positionCat = i;
					i++;
		        }
				

				this.choix = (Spinner) findViewById(R.id.choixUtilisateur);
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operation );
				// Specify the layout to use whintenten the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				this.choix.setAdapter(adapter);


				this.choixCat = (Spinner) findViewById(R.id.choixCategorie);
				// Create an ArrayAdapter using the string array and a default spinner layout
				adapterCategorie = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesString );
				// Specify the layout to use whintenten the list of choices appears
				adapterCategorie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				// Apply the adapter to the spinner
				this.choixCat.setAdapter(adapterCategorie);


				final Spinner repeter = (Spinner) findViewById(R.id.repeter);
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<String> adapterRecurrence = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, recurrentsString);
				// Specify the layout to use whintenten the list of choices appears
				adapterRecurrence.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				repeter.setAdapter(adapterRecurrence);

				//on récupère le choix de l'utilisateur (liste déroulante) 
				choix.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {

						choixOperation =(String)parent.getSelectedItem(); 

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}


				});

				
				//on récupère le choix de l'utilisateur (liste déroulante) 
				choixCat.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {

						choixCategorieId = parent.getSelectedItemId();
						//Toast.makeText(getApplicationContext(), String.valueOf(categoriesIds.get((int)choixCategorieId)), Toast.LENGTH_LONG).show();

						catDAO = new CategoryDAO(ModifierOperation.this);
						category = catDAO.selectionner(categoriesIds.get((int)parent.getSelectedItemId()));
//						System.out.println("Apres category selectionne : "+category.getId_category());
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}


				});

				category = getOperation.getCategory();
					
				//on récupère le choix de l'utilisateur (liste déroulante) 
				repeter.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {

						recurrence =(String)parent.getSelectedItem(); 

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}


				});

				// Fill the edittext and other stuff with datas
				this.montant.setText(String.valueOf(getOperation.getAmount()));
				this.libelle.setText(getOperation.getDescription());
//				this.date_begin.updateDate(2015, 0, 5);
				
				if (positionRec != -1)
					repeter.setSelection(positionRec);
				
//				Toast.makeText(ModifierOperation.this, String.valueOf(positionCat), Toast.LENGTH_LONG).show();
				if (positionCat != -1)
					choixCat.setSelection(positionCat);
				if(getOperation.getType().matches(operation[0])) 
					choix.setSelection(0);
				else
					choix.setSelection(1);
	
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id == R.id.modifierOperation){
			Intent  unIntent = new Intent(this, HomeActivity.class);
			
			Recurrence recurrenceChosen = new Recurrence();
			recurrenceChosen = recDAO.selectionnerParDescription(recurrence.toString());
			
			//m'occuper de l'ajout de la date;
			Date date = new Date();
			
			//pour éviter les erreurs
			if(this.montant.getText().toString().matches("")){
				this.montant.setText("0.0");
			}
			
			double amount = Double.parseDouble(this.montant.getText().toString());
			String description = this.libelle.getText().toString();
			getOperation.setType(choix.getSelectedItem().toString());
			getOperation.setAmount(amount);
			getOperation.setDescription(description);
			getOperation.setRecurrence(recurrenceChosen);
			getOperation.setCategory(category);
		
			operationDao.modifier(getOperation);
			
			this.startActivity(unIntent);
		}

		if(id == R.id.supprimerOperation){
			AlertDialog.Builder alertDelete = new AlertDialog.Builder(ModifierOperation.this);
			alertDelete.setPositiveButton("Oui", dialogPositiveListener);
			alertDelete.setNegativeButton("Non", dialogNegativeListener);
			alertDelete.setMessage("Voulez vous vraiment supprimer l'opération ?");
			alertDelete.show();
			
		}

		if(id == R.id.show_popup){
			//on créée la boîte de dialogue ainsi que les champs
			custom = new Dialog(ModifierOperation.this);
			custom.setContentView(R.layout.popup_ajout_categorie);
			ajoutCateg = (EditText)custom.findViewById(R.id.ajoutCateg);
			addbtn = (Button)custom.findViewById(R.id.addbtn);
			cancel = (Button)custom.findViewById(R.id.canbtn);
			custom.setTitle("Ajouter une catégorie : ");

			//Cette ligne permet de faire apparaître le clavier en appuyant sur l'editText
			final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

			ajoutCateg.setOnTouchListener(new OnTouchListener(){	
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (v == ajoutCateg)
					{
						mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
					}
					return false;
				}
			});

			//on rend les boutons écoutables
			addbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					newCateg = ajoutCateg.getText().toString();
					Category categ = new Category(newCateg);
					catDAO.ajouter(categ);
					adapterCategorie.clear();
			        categories = catDAO.selectionnerAll();
			        for(Category cat : categories) {
			        	categoriesString.add(cat.getDescription());
			        }
			        adapterCategorie.notifyDataSetChanged();
					custom.dismiss();
				}
			});
			
			supprimerOperation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					custom.dismiss();
				}
			});
			custom.show();
		}
	}

	public Dialog.OnClickListener dialogPositiveListener = new Dialog.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
//			budDao.supprimer(getBudget.getId_budget());
//			Intent unIntent = new Intent(BudgetDetails.this, HomeActivity.class);
//			startActivity(unIntent);
			operationDao.supprimer(getOperation.getId_operation());
			System.exit(0);
		}
	};

	public Dialog.OnClickListener dialogNegativeListener = new Dialog.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {			
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
//		MenuInflater inflater = getMenuInflater();
		// Instanciation du menu XML sp�cifier en un objet Menu
//		inflater.inflate(R.menu.activity_menu, menu);
		return true;
	}

}
