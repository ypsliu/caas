/**
 * 
 */
package cn.rongcapital.caas.test;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author zhaohai
 *
 */
public class ValidateRoleTreeXmlWithSchemaTest {

    @Test
    public void validate() throws SAXException, IOException, DocumentException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Source schemaSource = new StreamSource(
                this.getClass().getClassLoader().getResourceAsStream("schema/caas-role-tree.xsd"));
        Schema schema = factory.newSchema(schemaSource);
        Validator validator = schema.newValidator();
        Source xmlSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("role-tree.xml"));
        try {
            validator.validate(xmlSource);
            Assert.assertTrue(true);
        } catch (Exception ex) {
            Assert.assertTrue(false);
            ex.printStackTrace();
        }
    }
}
