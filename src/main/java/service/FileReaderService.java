package service;
/**
 *
 * @author Nathan
 */
import model.Disciplina;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderService {
    
     public static List<Disciplina> lerHorarios(String caminhoArquivo) {
        List<Disciplina> disciplinas = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                int codigo = (int) row.getCell(0).getNumericCellValue();
                String disciplina = row.getCell(1).getStringCellValue();
                int fase = (int) row.getCell(2).getNumericCellValue();
                int cargaHoraria = (int) row.getCell(3).getNumericCellValue();
                String professor = row.getCell(4).getStringCellValue();

                Disciplina disc = new Disciplina(codigo, disciplina, fase, cargaHoraria, professor);
                disciplinas.add(disc);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return disciplinas;
    }
}
