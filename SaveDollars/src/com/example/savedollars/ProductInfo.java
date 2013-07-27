/**************************************************************************************
Copyright (C) 2013 Smita Kundargi and Jeanne Betcy Victor

This program is free software: you can redistribute it and/or modify it under 
the terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. 
If not, see http://www.gnu.org/licenses/.

Author - Smita Kundargi and Jeanne Betcy Victor
email: ksmita@pdx.edu and jbv3@pdx.edu

 ******************************************************************************************/

package com.example.savedollars;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ProductInfo extends Activity {

	private List<Float> sortedList = new ArrayList<Float>();
	private Map merchantMap = new HashMap();
	private Map<Object, Object> sortedMap = new LinkedHashMap<Object, Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		System.out.println("Inside Product price display class");
		String barcodeNumber = (getIntent().getStringExtra("barcodeNumber"));
		getProductDetails(barcodeNumber);
		// Set the GooglePrice to lowest value

		super.onCreate(savedInstanceState);

		setContentView(R.layout.pdtpriceview);
		Iterator objMapIterator = sortedMap.entrySet().iterator();
		int i = 1;
		while (objMapIterator.hasNext()) {
			Map.Entry keyValuePairs = (Map.Entry) objMapIterator.next();
			if (i == 1) {
				final TextView textChange = (TextView) findViewById(R.id.merchant1PriceView);
				textChange.setText(String.valueOf(keyValuePairs.getValue()));
				final TextView merchantName = (TextView) findViewById(R.id.merchant1TextView);
				merchantName.setText(String.valueOf(keyValuePairs.getKey()));
				i++;
			} else if (i == 2) {
				final TextView textChange = (TextView) findViewById(R.id.merchant2PriceView);
				// textChange.setText(String.valueOf(sortedList.get(0)));
				textChange.setText(String.valueOf(keyValuePairs.getValue()));
				final TextView merchantName = (TextView) findViewById(R.id.merchant2TextView);
				merchantName.setText(String.valueOf(keyValuePairs.getKey()));

				i++;
			} else if (i == 3) {
				final TextView textChange = (TextView) findViewById(R.id.merchant3PriceView);
				// textChange.setText(String.valueOf(sortedList.get(0)));
				textChange.setText(String.valueOf(keyValuePairs.getValue()));
				final TextView merchantName = (TextView) findViewById(R.id.merchant3TextView);
				merchantName.setText(String.valueOf(keyValuePairs.getKey()));
				i++;

			} else if (i == 4) {
				final TextView textChange = (TextView) findViewById(R.id.merchant4PriceView);
				// textChange.setText(String.valueOf(sortedList.get(0)));
				textChange.setText(String.valueOf(keyValuePairs.getValue()));
				final TextView merchantName = (TextView) findViewById(R.id.merchant4TextView);
				merchantName.setText(String.valueOf(keyValuePairs.getKey()));

			}

		}

	}

	private void getProductDetails(String barcodeNumber) {
		// Smita

		String baseURL = getString(R.string.searchURL);
		String key = getString(R.string.key);
		String country = getString(R.string.country);
		String urlString = baseURL + "&" + key + "&" + country + "&" + "q="
				+ barcodeNumber;
		System.out.println("Bets1 : urlString:" + urlString);

		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			System.out.println("Bets2 : url:" + url);
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder out = new StringBuilder();
			String line;
			String data;
			while ((line = reader.readLine()) != null) {
				System.out.println("Bets3 : line:" + line);
				out.append(line);
			}
			// JSON data stored as string.
			data = out.toString();
			System.out.println("Bets: JSON DATA :" + data); // product
															// information from
															// google API in
			// JSON format.
			// Convert to JSON object for parsing
			JSONObject jsonResponse = new JSONObject(data);
			// Get the names
			JSONArray arr = jsonResponse.names();
			System.out.println("Bets arr:" + arr);
			// JSONArray ItemCount =
			// jsonResponse.getJSONArray("totalItems");//bets
			// System.out.println("BETS totalItems :"+ItemCount);//bets
			JSONArray parsedItems = jsonResponse.getJSONArray("items");
			JSONObject inventory = null;
			// JSONObject inventory = parsedItems.getJSONObject("inventories");

			for (int j = 0; j < parsedItems.length(); j++) {

				inventory = parsedItems.getJSONObject(j);

				JSONObject objPrice = inventory.getJSONObject("product");
				JSONObject merchant = objPrice.getJSONObject("author");
				String merchantName = merchant.getString("name");
				// JSONArray totalItems;// = arr.getString("totalItems");

				System.out.println("MERCHANT NAME = " + merchantName);
				// JSONArray merchantArray = merchant.getJSONArray("name");
				JSONArray invObj = objPrice.getJSONArray("inventories");
				System.out.println("merchant  array length is : "
						+ merchant.toString());
				System.out.println("invObj length is : " + invObj.length());

				for (int z = 0; z < invObj.length(); z++) {
					JSONObject price = invObj.getJSONObject(z);
					System.out.println(" Json object price is: "
							+ price.toString());
					String productPrice = price.getString("price");
					String shipping = price.getString("shipping");
					float finalPrice = Float.parseFloat(productPrice)
							+ Float.parseFloat(shipping);

					System.out.println("Final price is " + finalPrice);
					merchantMap.put(merchantName, finalPrice);
					sortedList.add(Float.valueOf(finalPrice));

				}

			}

			Collections.sort(sortedList);

			System.out.println("Lowest price is " + sortedList.get(0));
			sortMerchantPrices();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * method : sortMerchantPrices arguments : none description: Sorts a hashmap
	 * containing merchant names and prices by values ( prices ). The idea is to
	 * convert map to list and then sort before converting back to map again.
	 * returns : void
	 */
	private void sortMerchantPrices() {

		List objList = new LinkedList(merchantMap.entrySet());

		Collections.sort(objList, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		for (Iterator it = objList.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * public void onClick(View v) { switch (v.getId()) { case R.id.menuButton:
	 * System.out.println("At main menu button"); Intent mainMenuIntent = new
	 * Intent(ProductPriceDisplay.this, MainActivity.class);
	 * startActivity(mainMenuIntent);
	 * 
	 * break; } }
	 */
	public void activity_main(View v) {
		finish();
	}

}
