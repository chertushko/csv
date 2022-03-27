import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv", true))) {
            writer.writeNext(employee1);
            writer.writeNext(employee2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        System.out.println("parseCSV" + list);
        String json = listToJson(list);
        System.out.println("json" + json);
        writeString(json, "data1.json");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element staff = document.createElement("staff");
        document.appendChild(staff);
        Element employee = document.createElement("employee");
        staff.appendChild(employee);
        Element employeeFirst = document.createElement("employee");
        employee.appendChild(employeeFirst);
        addElements(document, employeeFirst, columnMapping, employee1);
        Element employeeSecond = document.createElement("employee");
        addElements(document, employeeSecond, columnMapping, employee2);
        employee.appendChild(employeeSecond);

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);

        List<Employee> listXML = parseXML("data.xml");
        System.out.println("parseXML" + listXML);
        String json2 = listToJson(listXML);
        System.out.println("json2" + json2);
        writeString(json2, "data2.json");

        // С помощью ранее написанного метода listToJson() преобразуйте список в JSON и запишите его в файл c помощью метода writeString().

    }

    public static void addElements(Document document, Element employee, String[] name, String[] value) {
        for (int i = 0; i < name.length; i++) {
            Element id = document.createElement(name[i]);
            id.appendChild(document.createTextNode(value[i]));
            employee.appendChild(id);
        }
    }


    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String str, String fileJson) {
        try (FileWriter file = new FileWriter(fileJson)) {
            file.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(file));
        Node node = document.getDocumentElement();
        NodeList nodeList = node.getChildNodes();
        List<Employee> staff = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nodes = nodeList.item(i);
            if (Node.ELEMENT_NODE == nodes.getNodeType()) {
                Element element = (Element) nodes;
                staff.add(new Employee(Long.parseLong(element.getElementsByTagName("id").item(i).getTextContent()),
                        (element.getElementsByTagName("firstName").item(i).getTextContent()),
                        (element.getElementsByTagName("lastName").item(i).getTextContent()),
                        (element.getElementsByTagName("country").item(i).getTextContent()),
                        Integer.parseInt(element.getElementsByTagName("age").item(i).getTextContent())));
                System.out.println("add" + staff);
            }
        }
        return staff;
    }

}




