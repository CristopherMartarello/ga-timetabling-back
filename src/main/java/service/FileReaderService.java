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
import model.Professor;

public class FileReaderService {

    public static List<Disciplina> lerHorarios(String caminhoArquivo) {
        List<Disciplina> disciplinas = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(caminhoArquivo); Workbook workbook = new XSSFWorkbook(fis)) {

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

    public static List<Professor> lerHorariosProfessores(String caminhoArquivo) {
        List<Professor> professores = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(caminhoArquivo); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                ArrayList horarios = new ArrayList();
                horarios.clear();

                String nome = row.getCell(0).getStringCellValue();
                horarios.add((int) row.getCell(1).getNumericCellValue());
                horarios.add((int) row.getCell(2).getNumericCellValue());
                horarios.add((int) row.getCell(3).getNumericCellValue());
                horarios.add((int) row.getCell(4).getNumericCellValue());
                horarios.add((int) row.getCell(5).getNumericCellValue());

                Professor prof = new Professor(nome, horarios);
                professores.add(prof);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return professores;
    }
}
