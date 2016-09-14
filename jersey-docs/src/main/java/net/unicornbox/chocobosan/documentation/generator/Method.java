package net.unicornbox.chocobosan.documentation.generator;

import java.io.BufferedReader;
import java.io.IOException;

public class Method
{
    private String path;

    private String consumes;

    private String produces;

    private String methodType;

    private String documentation;

    private String since;

    public Method(BufferedReader br) throws IOException
    {
        // fill the fields with the br
        String line = "";
        while ((line = br.readLine()) != null && (!line.contains("public")))
        {
            if (line.contains("@Path"))
            {
                path = GenerateRestDocumentation.parsePathName(line, br);
            }
            else if (line.contains("@Produces"))
            {
                produces = getConsumesOrProduces(line, br);
            }
            else if (line.contains("@Consumes"))
            {
                consumes = getConsumesOrProduces(line, br);
            }
            else if (line.contains("@Docs"))
            {
                documentation = GenerateRestDocumentation.parsePathName(line, br);
            }
            else if (line.contains("@Since"))
            {
                since = GenerateRestDocumentation.parsePathName(line, br);
            }
            else if (line.contains("@"))
            {
                // it's the type of method
                methodType = line.split("\\@")[1];
            }
            else
            {
                break;
            }
        }
    }

    private String getConsumesOrProduces(String line, BufferedReader br) throws IOException
    {
        if (line.endsWith(","))
        {
            line += br.readLine();
        }
        String parsedString = "";
        if (line.contains("APPLICATION_XML"))
        {
            parsedString += "APPLICATION_XML" + ",";
        }
        if (line.contains("APPLICATION_JAVA_SERIALIZED_OBJECT"))
        {
            parsedString += "APPLICATION_JAVA_SERIALIZED_OBJECT" + ",";
        }

        // remove tray comma
        if (parsedString.isEmpty())
        {
            return null;
        }
        parsedString = parsedString.substring(0, parsedString.length() - 1);
        return parsedString;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getConsumes()
    {
        return consumes;
    }

    public void setConsumes(String consumes)
    {
        this.consumes = consumes;
    }

    public String getProduces()
    {
        return produces;
    }

    public void setProduces(String produces)
    {
        this.produces = produces;
    }

    public String getMethodType()
    {
        return methodType;
    }

    public void setMethodType(String methodType)
    {
        this.methodType = methodType;
    }

    public String getDocumentation()
    {
        return documentation;
    }

    public void setDocumentation(String documentation)
    {
        this.documentation = documentation;
    }

    public String getSince()
    {
        return since;
    }

    public void setSince(String since)
    {
        this.since = since;
    }
}
