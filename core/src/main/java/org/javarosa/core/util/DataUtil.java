package org.javarosa.core.util;

import org.javarosa.core.model.utils.DateUtils;

import java.util.Vector;

/**
 * @author ctsims
 */
public class DataUtil {
    static final int offset = 10;
    static final int low = -10;
    static final int high = 400;
    static Integer[] iarray;

    static UnionLambda unionLambda = new UnionLambda();
    static StringSplitter stringSplitter = new StringSplitter();

    /**
     * Get Integer object that corresponds to int argument from a
     * pre-computed table, or build a new instance.
     *
     * @return Cached or new Integer instance that correpsonds to ivalue argument
     */
    public static Integer integer(int ivalue) {
        // lazily populate Integer cache
        if (iarray == null) {
            iarray = new Integer[high - low];
            for (int i = 0; i < iarray.length; ++i) {
                iarray[i] = new Integer(i + low);
            }
        }

        if (ivalue < high && ivalue >= low) {
            return iarray[ivalue + offset];
        } else {
            return new Integer(ivalue);
        }
    }

    public static <T> Vector<T> union(Vector<T> a, Vector<T> b) {
        return unionLambda.union(a, b);
    }

    public static void setUnionLambda(UnionLambda newUnionLambda) {
        unionLambda = newUnionLambda;
    }

    public static class UnionLambda {
        public <T> Vector<T> union(Vector<T> a, Vector<T> b) {
            Vector<T> u = new Vector<T>();
            //Efficiency?
            for (T i : a) {
                if (b.contains(i)) {
                    u.addElement(i);
                }
            }
            return u;
        }
    }

    public static String[] splitOnSpaces(String s) {
        return stringSplitter.splitOnSpaces(s);
    }

    public static String[] splitOnDash(String s) {
        return stringSplitter.splitOnDash(s);
    }

    public static String[] splitOnColon(String s) {
        return stringSplitter.splitOnColon(s);
    }

    public static String[] splitOnPlus(String s) {
        return stringSplitter.splitOnPlus(s);
    }

    public static void setStringSplitter(StringSplitter newStringSplitter) {
        stringSplitter = newStringSplitter;
    }

    public static class StringSplitter {

        public String[] splitOnSpaces(String s) {
            Vector<String> vectorSplit = DateUtils.split(s, " ", true);
            return vectorSplit.toArray(new String[vectorSplit.size()]);
        }

        public String[] splitOnDash(String s) {
            Vector<String> vectorSplit = DateUtils.split(s, "-", false);
            return vectorSplit.toArray(new String[vectorSplit.size()]);
        }

        public String[] splitOnColon(String s) {
            Vector<String> vectorSplit = DateUtils.split(s, ":", false);
            return vectorSplit.toArray(new String[vectorSplit.size()]);
        }

        public String[] splitOnPlus(String s) {
            Vector<String> vectorSplit = DateUtils.split(s, "+", false);
            return vectorSplit.toArray(new String[vectorSplit.size()]);
        }
    }

}
