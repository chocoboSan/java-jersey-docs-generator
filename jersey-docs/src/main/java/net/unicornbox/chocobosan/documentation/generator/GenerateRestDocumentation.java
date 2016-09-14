package net.unicornbox.chocobosan.documentation.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GenerateRestDocumentation
{

    private static String packageName = "";

    private final static String packageKey = "packagePath";

    private static String outputBaseHTML = "doc";

    private static String outputBasePath = "target/";

    private static String baseTitle = "Rest API Documentation";

    public static void main(String[] args) throws IOException
    {
        packageName = System.getProperty(packageKey);
        if (packageName == null || packageName.isEmpty())
        {
            System.err.println("Error : usage => -DpackageName=\"path/to/javafiles/to/parse/\"");
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputBasePath + outputBaseHTML + ".html")));
        String header = generateHeader(baseTitle);
        bw.write(header);
        bw.write("<h1 align=\"center\">Welcome to Rest API documentation</h1>");
        String[] listFiles = new File(packageName).list();
        Map<String, String> resourcesParsed = new TreeMap<String, String>();
        if (listFiles != null)
        {
            for (String filename : listFiles)
            {
                if (filename.contains("Resource"))
                {
                    handleResource(packageName + filename, resourcesParsed);
                }
            }
        }
        for (String resource : resourcesParsed.keySet())
        {
            BufferedWriter bwLoc = new BufferedWriter(new FileWriter(
                    new File(outputBasePath + outputBaseHTML + resource.replaceAll("/", "_") + ".html")));
            String headerLoc = generateHeader(baseTitle + " : " + resource);
            bwLoc.write(headerLoc);
            bwLoc.write(resourcesParsed.get(resource));
            bwLoc.write(getFooter());
            bwLoc.close();
            bw.write("<h1  align=\"center\"><a href=\"" + outputBaseHTML + resource.replaceAll("/", "_") + ".html"
                    + "\" style=\"border: 1px solid\">" + resource + "</a></h1>");
        }
        System.out.println("finished parsing documentation");

        bw.write(getFooter());
        bw.close();
    }

    private static String getFooter()
    {
        return "</body></html>";
    }

    private static String generateHeader(String title)
    {
        return "<html>" + "<title>" + title + "</title>" + "<meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">"
                + "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>"
                + "<script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>"
                + ("<body>");
    }

    private static void handleResource(String filename, Map<String, String> resourcesParsed) throws IOException
    {
        String parsedResource = "";
        String resourceFromMap = null;
        BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
        String line = "";
        String resourceName = null;
        List<Method> methods = new ArrayList<Method>();
        while ((line = br.readLine()) != null)
        {
            if (resourceName == null)
            {
                if (line.contains("@Path"))
                {
                    resourceName = parsePathName(line, br);
                    resourceFromMap = resourcesParsed.get(resourceName);
                }
            }
            else
            {
                if (line.contains("@Request"))
                {
                    // create a new Method
                    Method m = new Method(br);
                    methods.add(m);
                }
            }
        }
        if (resourceFromMap == null)
        {
            parsedResource += ("<h2>Resource : http://rendition_url:rest_port" + resourceName + "</h2>");
        }
        parsedResource += ("<table class=\"table table-striped table-hover table-condensed table-bordered\">");
        parsedResource += ("<thead>" + "<tr>" + "<th>Path</th>" + "<th>Since</th>" + "<th>Method type</th>"
                + "<th>Consumes</th>" + "<th>Produces</th>" + "<th>Description</th>" + "</tr>" + "</thead>");
        parsedResource += ("<tbody>");
        for (Method m : methods)
        {
            parsedResource += ("<tr>");
            parsedResource += ("<td>" + notIfNull(m.getPath()) + "</td>");
            parsedResource += ("<td>" + notIfNull(m.getSince()) + "</td>");
            parsedResource += ("<td" + getBackgroundType(m.getMethodType()) + ">" + notIfNull(m.getMethodType())
                    + "</td>");
            parsedResource += ("<td>" + notIfNull(m.getConsumes()) + "</td>");
            parsedResource += ("<td>" + notIfNull(m.getProduces()) + "</td>");
            parsedResource += ("<td>" + notIfNull(m.getDocumentation()) + "</td>");
            parsedResource += ("</tr>");
        }
        parsedResource += ("</tbody>");
        parsedResource += ("</table>");
        if (resourceFromMap != null)
        {
            resourceFromMap += parsedResource;
        }
        else
        {
            resourceFromMap = parsedResource;
        }
        resourcesParsed.put(resourceName, resourceFromMap);
    }

    private static String getBackgroundType(String methodType)
    {
        if (methodType == null)
        {
            return "";
        }
        if (methodType.equals("POST"))
        {
            return " style=\"background-color: #10a53F;\" ";
        }
        if (methodType.equals("GET"))
        {
            return " style=\"background-color: #009999;\" ";
        }
        if (methodType.equals("DELETE"))
        {
            return " style=\"background-color: #a41e22;\" ";
        }
        return "";
    }

    private static String notIfNull(String path)
    {
        return (path == null) ? "-" : path;
    }

    public static String parsePathName(String line, BufferedReader br) throws IOException
    {
        line = line.replaceAll("\"", "");
        while (!contains(line, ')'))
        {
            line = line.replaceAll("\\+", "") + br.readLine().replaceAll("\\+", "").replaceAll("\"", "");
        }
        return line.split("\\(")[1].split("\\)")[0];
    }

    private static boolean contains(String line, char c)
    {
        for (int i = 0; i < line.length(); i++)
        {
            if (line.charAt(i) == c)
            {
                return true;
            }
        }
        return false;
    }

}
