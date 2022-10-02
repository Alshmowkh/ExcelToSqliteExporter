package com.alshmowkh.exceltosqliteexporter;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    TextView txt1;
    private SQLiteDatabase db;
    private StringBuffer sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String appTitle = "محول بيانات Excel إلى Sqlite";
        setTitle(appTitle);

        txt1 = findViewById(R.id.txt1);
        txt1.setMovementMethod(new ScrollingMovementMethod());

        Sheet sheet = initialExcel();
//        DhiahFactory(sheet);
        Map[] dataMaps = dataFactory(sheet);
        db = initialDB();
        bindingDB(dataMaps);
//        txt1.setText(db + "");
    }

    private void bindingDB(Map[] dataMaps) {
        if (!db.isOpen()) return;

        sb = new StringBuffer();
        Map dhiah = dataMaps[0];
        Map gacim = dataMaps[1];
        Map appendix = dataMaps[2];
        Map enumeration = dataMaps[3];

        //bindDhiah(dataMaps[0]);
//        bindGacim(dataMaps[1]);
//        bindAppendix(dataMaps[2]);
//        bindMain(dataMaps[2]);
//        bindEnumeration(dataMaps[3]);


        showTable("dhiah");

    }

    private void bindDhiah(Map dataMap) {
        //       db.execSQL("delete from Dhiah");
        dataMap.forEach((k, v) -> db.execSQL("insert into dhiah values(" + k + ",'" + v + "',null)"));
    }

    private void bindGacim(Map dataMap) {
        AtomicInteger i = new AtomicInteger();
        dataMap.forEach((k, v) -> {
            int gaId = Character.getNumericValue(k.toString().toCharArray()[k.toString().trim().length() - 1]);
            int dhId = Integer.parseInt(k.toString().substring(0, k.toString().length() - 1));
            db.execSQL("insert into gacim values(" + k + ",'" + v + "',null," + dhId + ")");
            //sb.append(i.getAndIncrement() + "----:" + dhId + "--" + gaId + "-------" + v + "\n");
        });

    }

    private void bindAppendix(Map dataMap) {

        dataMap.forEach((k, v) -> {
            if (!v.toString().trim().isEmpty()) {
                int apId = Character.getNumericValue(k.toString().toCharArray()[k.toString().trim().length() - 1]);
                int gaId = Character.getNumericValue(k.toString().toCharArray()[k.toString().trim().length() - 2]);
                int dhId = Integer.parseInt(k.toString().substring(0, k.toString().length() - 2));
                String type = getTypeAppendix(v);
                AtomicInteger i = new AtomicInteger();
                sb.append(i.getAndIncrement() + "-:" + k + "---" + v + "----" + type + "\n");
                int gaIdParsed = Integer.parseInt(dhId + "" + gaId);
                db.execSQL("insert into appendix values(" + k + ",'" + type + "','" + v + "'," + gaIdParsed + ")");
            }
        });
    }

    // for binding main table..............
    private void bindMain(Map dataMap) {

//        db.execSQL("delete from main");

        dataMap.forEach((k, v) -> {

            int apId = Character.getNumericValue(k.toString().toCharArray()[k.toString().trim().length() - 1]);
            int gaId = Character.getNumericValue(k.toString().toCharArray()[k.toString().trim().length() - 2]);
            int dhId = Integer.parseInt(k.toString().substring(0, k.toString().length() - 2));

            db.execSQL("insert into main values(" + k + "," + dhId + "," + gaId + "," + apId + ")");

        });
    }

    // binding Enumeration table....

    private void bindEnumeration(Map dataMap) {

        //db.execSQL("delete from enumeration");

        String[] sex = new String[]{"لوز", "قات", "مساحة-تقريبية", "مساحة-الدليل", "مساحة-الواقع", "تكدوف"};
        String[] units = new String[]{"شجرة", "مغرس", "لبنة"};
        dataMap.forEach((k, v) -> {

            Object[] amounts = (Object[]) v;
            int almonds = (int) amounts[0];
            int gaat = (int) amounts[1];
            double areaBeta = (double) amounts[2];

            //  sb.append(k + "-" + almonds + "--" + gaat + "---" + areaBeta + "\n");

            db.execSQL("insert into Enumeration values(" + k + ",'" + sex[0] + "'," + almonds + ",'" + units[0] + "')");
            db.execSQL("insert into Enumeration values(" + k + ",'" + sex[1] + "'," + gaat + ",'" + units[1] + "')");
            db.execSQL("insert into Enumeration values(" + k + ",'" + sex[2] + "'," + areaBeta + ",'" + units[2] + "')");

        });
    }

    private String getTypeAppendix(Object v) {
        String type = "unknown";
        String value = v.toString();

        if (value.contains("حقبة")) return "Hagba";
        if (value.contains("خروجة")) return "Kharoja";
        if (value.contains("سبة")) return "Sabba";
        if (value.contains("معلقة")) return "Moallga";
        return type;
    }

    private void showTable(String table) {
        Cursor cursor;
        sb = new StringBuffer();
        cursor = db.rawQuery("select * from " + table, null);

        int id;
        String name;
        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
            name = cursor.getString(1);
            sb.append(id + "-------" + name + "\n");

        }
        txt1.setText(sb.toString());
    }

    private SQLiteDatabase initialDB() {
        SQLiteDatabase db;
        String dbPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Safat-androidProject/database/SafatDB-2.db";
        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
//        txt1.setText(db+"");
        return db;
    }


    void DhiahFactory(Sheet sheet) {

        Cell cell;

        Row row;
        Object cellV;

        Iterator<Row> itr;
        itr = sheet.iterator();
        Iterator cells;

        Map map = new HashMap();
        while (itr.hasNext()) {
            cellV = null;
            row = itr.next();

            if (row.getRowNum() < 3) continue;
            cells = row.cellIterator();
            cells.next();
            cells.next();
            cell = (Cell) cells.next();

            if (cell.getCellType() == 0) {
                cellV = ((Double) cell.getNumericCellValue()).intValue();

            }
            if (cell.getCellType() == 1) {
                cellV = Integer.getInteger(cell.getStringCellValue());
            }

            if (cellV != null) {
                cells.next();
                cells.next();
                cell = (Cell) cells.next();
                cell.setCellType(1);
                String dhName = cell.getStringCellValue();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    map.putIfAbsent(cellV, dhName);
                }
            }
        }
        txt1.setText(map.toString());
        txt1.setMovementMethod(new ScrollingMovementMethod());
    }

    private Map[] dataFactory(Sheet sheet) {

        StringBuilder sb = new StringBuilder();
        Cell dhId, gaId, apId;
        Cell dhName, gaName, apName;
        Cell enAl, enGa, enArBeta;

        int dhIdV, gaIdV, apIdV;
        String dhNameV, gaNameV, apNameV;
        int enAlV, enGaV;
        double enArBetaV;
        int idGen;
        int[] ids = new int[3];
        Row row;

        HashSet hashSet = new HashSet();
        Iterator<Row> rowsItr = sheet.iterator();
        Iterator<Cell> cellItr;
        List list = new ArrayList();
        Map dhMap = new HashMap();
        Map gaMap = new HashMap();
        Map apMap = new HashMap();
        Map enMap = new HashMap();

        while (rowsItr.hasNext()) {

            row = rowsItr.next();

            if (row.getRowNum() < 3) continue;
            if (row.getRowNum() > 218) continue;
            cellItr = row.cellIterator();
            cellItr.next();
            cellItr.next();

            dhId = cellItr.next();
            dhId.setCellType(0);
            dhIdV = ((Double) dhId.getNumericCellValue()).intValue();

            gaId = cellItr.next();
            gaId.setCellType(0);
            gaIdV = ((Double) gaId.getNumericCellValue()).intValue();

            apId = cellItr.next();
            apId.setCellType(0);
            apIdV = ((Double) apId.getNumericCellValue()).intValue();

            dhName = cellItr.next();
            dhName.setCellType(1);
            dhNameV = dhName.getStringCellValue().trim();

            gaName = cellItr.next();
            gaName.setCellType(1);
            gaNameV = gaName.getStringCellValue().trim();

            apName = cellItr.next();
            apName.setCellType(1);
            apNameV = apName.getStringCellValue().trim();

            enAl = cellItr.next();
            enAl.setCellType(0);
            enAlV = ((Double) enAl.getNumericCellValue()).intValue();

            enGa = cellItr.next();
            enGa.setCellType(0);
            enGaV = ((Double) enGa.getNumericCellValue()).intValue();

            enArBeta = cellItr.next();
            enArBeta.setCellType(0);
            enArBetaV = enArBeta.getNumericCellValue();
            ids[0] = dhIdV;
            ids[1] = gaIdV;
            ids[2] = apIdV;
            idGen = Integer.parseInt(dhIdV + "" + gaIdV + "" + apIdV);

            dhMap.putIfAbsent(dhIdV, dhNameV);
            gaMap.putIfAbsent(Integer.parseInt(dhIdV + "" + gaIdV), gaNameV);
            apMap.putIfAbsent(idGen, apNameV);
            enMap.putIfAbsent(idGen, new Object[]{enAlV, enGaV, enArBetaV});

            //System.out.println(apIdV +"--------:"+key+":--------"+row.getRowNum());
//            sb.append(idGen + "\t" + apNameV + "\t" + enAlV + "\n");

        }

//        System.out.println(sb.toString());
//        txt1.setText(sb.toString());
//        txt1.setMovementMethod(new ScrollingMovementMethod());
        return new Map[]{dhMap, gaMap, apMap, enMap};
    }

    private Sheet initialExcel() {
        String excelFilePath = "", excelFileName;
        excelFileName = "excelFile-1.xlsx";
        excelFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        excelFilePath += File.separator + excelFileName;
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(excelFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wb == null) {
            txt1.setText("error occurred");
            return null;
        }
        Sheet sheet = wb.getSheetAt(1);
        //DhiahFactory dhFactory=new DhiahFactory(sheet);
        //List<Dhiah> dhiahs=dhFactory.getDhiahs();
        //txt1.setText(sheet.getSheetName()+"");
        return sheet;
    }
}