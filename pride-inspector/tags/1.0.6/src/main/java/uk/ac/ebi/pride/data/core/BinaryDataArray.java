package uk.ac.ebi.pride.data.core;

import java.util.Arrays;

/**
 * BinaryDataArray is a slim down version of binaryDataArray in mzML
 * ParamGroup of this object must have the followings in mzML 1.1.0.1 definition:
 * <p/>
 * For Chromatograms (once each):
 * 1. a child term of binary data compression type (zlib compression, no compression)
 * 2. a child term of binary data array (m/z array, intensity array, charge array,
 * signal to noise array, time array, wavelength array, non-standard data array,
 * flow rate array, pressure array and temperature array)
 * 3. a child term of binary data type (32-bit float or 64-bit float)
 * <p/>
 * For Spectrum (once each):
 * 1. a child term of binary data compression type (zlib compression, no compression)
 * 2. a child term of binary data array (m/z array, intensity array, charge array and signal to noise array)
 * 3. a child term of binary data type (32-bit float or 64-bit float)
 * <p/>
 * Note: arrayLength is ignored
 * Note: encodedLength is ignored
 * Note: In mzML, binary is always base64 encoded and is always "little endian".
 * <p/>
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 14:13:09
 */
public class BinaryDataArray extends ParamGroup {

    /**
     * DataProcessing Object
     */
    private DataProcessing dataProcessing = null;
    /**
     * Binary double array
     */
    private double[] binaryDoubleArray = null;

    /**
     * Constructor
     *
     * @param dataProcessing  optional.
     * @param binaryDoubleArr required.
     * @param params          required, but there is no way of enforce/check it.
     */
    public BinaryDataArray(DataProcessing dataProcessing, double[] binaryDoubleArr, ParamGroup params) {
        super(params);
        setDataProcessing(dataProcessing);
        setDoubleArray(binaryDoubleArr);
    }

    public double[] getDoubleArray() {
        return Arrays.copyOf(binaryDoubleArray, binaryDoubleArray.length);
    }

    public void setDoubleArray(double[] binaryDoubleArr) {
        this.binaryDoubleArray = Arrays.copyOf(binaryDoubleArr, binaryDoubleArr.length);
    }

    public DataProcessing getDataProcessing() {
        return dataProcessing;
    }

    public void setDataProcessing(DataProcessing dataProcessing) {
        this.dataProcessing = dataProcessing;
    }
}
