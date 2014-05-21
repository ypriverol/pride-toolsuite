//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.08.27 at 05:05:16 PM CEST 
//


package uk.ac.ebi.pride.pia.modeller.execute.xmlparams;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PossibleITEMType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PossibleITEMType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="int"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="float"/>
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="int-pair"/>
 *     &lt;enumeration value="double-pair"/>
 *     &lt;enumeration value="output-prefix"/>
 *     &lt;enumeration value="input-file"/>
 *     &lt;enumeration value="output-file"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PossibleITEMType")
@XmlEnum
public enum PossibleITEMType {

    @XmlEnumValue("int")
    INT("int"),
    @XmlEnumValue("double")
    DOUBLE("double"),
    @XmlEnumValue("float")
    FLOAT("float"),
    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("int-pair")
    INT_PAIR("int-pair"),
    @XmlEnumValue("double-pair")
    DOUBLE_PAIR("double-pair"),
    @XmlEnumValue("output-prefix")
    OUTPUT_PREFIX("output-prefix"),
    @XmlEnumValue("input-file")
    INPUT_FILE("input-file"),
    @XmlEnumValue("output-file")
    OUTPUT_FILE("output-file");
    private final String value;

    PossibleITEMType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PossibleITEMType fromValue(String v) {
        for (PossibleITEMType c: PossibleITEMType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
