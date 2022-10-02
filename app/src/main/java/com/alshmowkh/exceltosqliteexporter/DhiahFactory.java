package com.alshmowkh.exceltosqliteexporter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class DhiahFactory {
    private Sheet sheet;

    public DhiahFactory(Sheet sheet) {
        this.sheet = sheet;
        binding();
    }

    private void binding() {
        for(Row row:sheet){

            for(Cell cell:row){
                Cell dhID;
                if(cell.getColumnIndex()==2){

                }
            }
        }
    }

    public List<Dhiah> getDhiahs() {
        return null;
    }
}
