/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Defines constants and methods for SCOPUS ASJC.
 * Journal Classes are in
 * C:\NETWORK_DATA\Elsevier\SCOPUS_Journal_Classification_title_list.xlsx
 * downloaded from SCOPUS web pages.
 * ASJC codes are four digits (no leading zeros) mmnn. Not all possible four
 * digit codes are used.
 * There is a three level hierarchy.
 * The top level or type is one  of four main types, plus general and unknown.
 * The mid level is encoded by the first two digits, mm.  The internal index
 * is simply [mm-10] as 1000 is the general mid level class.
 * The bottom level is the finest level requiring all four digits.
 * Internally zero index is always the general class at all three levels.
 * This is an official ASJC code as the first one is 1000 and there are no other
 * 10nn entries. The last index at each level is always
 * an additional non-SCOPUS class unknown.  This corresponds to 3700 as the last
 * official codes are 36nnmid.
 * {@link http://www.info.sciverse.com/documents/files/scopus-training/resourcelibrary/xls/title_list.xlsx}
 * @author time
 */
public class ASJCclasses {

    /**
     * Top level classes or types.
     * Last one is always the unknown type.
     */
    public final static String [] topLevelNames =
   {"General",
    "Life Sciences",
    "Social Sciences",
    "Physical Sciences",
    "Health Sciences",
    "Unknown"}; // unknown is always last entry


    public static String getAllTypes(String sep){
        String s="";
        for (int t=0; t<topLevelNames.length; t++) s=s+(t==0?"":sep)+topLevelNames[t];
        return s;
    }

    /**
     * Top Level Index of unknown type.
     * {@value}
     */
    public final static int Unknown_TYPE=topLevelNames.length-1;
    /**
     * Top Level Index of General type.
     * {@value}
     */
    public final static int General_TYPE=0;
    /**
     * Top Level Index of Life Sciences type.
     * {@value}
     */
    public final static int Life_Sciences_TYPE=1;
    /**
     * Top Level Index of Social Sciences type.
     * {@value}
     */
    public final static int Social_Sciences_TYPE=2;
    /**
     * Top Level Index of Physical Sciences type.
     * {@value}
     */
    public final static int Physical_Sciences_TYPE=3;
    /**
     * Top Level Index of Health Sciences type.
     * {@value}
     */
    public final static int Health_Sciences_TYPE=4;


    /**
     * Mid level names.
     * All ASJC codes with same first two digits
     * are in same mid level.  So if ASJC is mmnn
     * then name is midLevelNames[mm-10].
     * First is general last is unknown mid level type.
     */
    public final static String [] midLevelNames = {
 "General",
 "Agricultural and Biological Sciences",
 "Arts and Humanities",
 "Biochemistry",
 "Genetics and Molecular Biology",
 "Business",
 "Management and Accounting",
 "Chemical Engineering",
 "Chemistry",
 "Computer Science",
 "Decision Sciences",
 "Earth and Planetary Sciences",
 "Economics",
 "Econometrics and Finance",
 "Energy",
 "Engineering",
 "Environmental Science",
 "Immunology and Microbiology",
 "Materials Science",
 "Mathematics",
 "Medicine",
 "Neuroscience",
 "Nursing",
 "Pharmacology",
 "Toxicology and Pharmaceutics",
 "Physics and Astronomy",
 "Psychology",
 "Social Sciences",
 "Veterinary",
 "Dentistry",
 "Health Professions",
    "Unknown"}; // unknown is always last entry

    /**
     * Mid Level Index of unknown type.
     * {@value}
     */
    final static int Unknown_MidLevelIndex=midLevelNames.length-1;

        /**
     * Links internal mid level index to top level index.
     * The mid level index is (mm-10) for ASJC of form mmnn.
     */
    public final static int [] midToTop =
   {0, 1, 2, 1, 2, 3, 3, 3, 2, 3,
    2, 3, 3, 3, 1, 3, 3, 4, 1, 4,
    1, 3, 2, 2, 4, 4, 4, 5};



    /**
     * Bottom level names.
     * The bottom index b is converted to ASJC using
     * indexToASJC[b]=mmnn and the name for this bottom level is
     * ASJCNames[b].
     */
    public final static String [] ASJCNames = {
        "General",
 "Agricultural and Biological Sciences(all)",
 "Agricultural and Biological Sciences (miscellaneous)",
 "Agronomy and Crop Science",
 "Animal Science and Zoology",
 "Aquatic Science",
 "Ecology, Evolution, Behavior and Systematics",
 "Food Science",
 "Forestry",
 "Horticulture",
 "Insect Science",
 "Plant Science",
 "Soil Science",
 "Arts and Humanities(all)",
 "Arts and Humanities (miscellaneous)",
 "History",
 "Language and Linguistics",
 "Archaeology",
 "Classics",
 "Conservation",
 "History and Philosophy of Science",
 "Literature and Literary Theory",
 "Museology",
 "Music",
 "Philosophy",
 "Religious studies",
 "Visual Arts and Performing Arts",
 "Biochemistry, Genetics and Molecular Biology(all)",
 "Biochemistry, Genetics and Molecular Biology (miscellaneous)",
 "Ageing",
 "Biochemistry",
 "Biophysics",
 "Biotechnology",
 "Cancer Research",
 "Cell Biology",
 "Clinical Biochemistry",
 "Developmental Biology",
 "Endocrinology",
 "Genetics",
 "Molecular Biology",
 "Molecular Medicine",
 "Physiology",
 "Structural Biology",
 "Business, Management and Accounting(all)",
 "Business, Management and Accounting (miscellaneous)",
 "Accounting",
 "Business and International Management",
 "Management Information Systems",
 "Management of Technology and Innovation",
 "Marketing",
 "Organizational Behavior and Human Resource Management",
 "Strategy and Management",
 "Tourism, Leisure and Hospitality Management",
 "Industrial relations",
 "Chemical Engineering(all)",
 "Chemical Engineering (miscellaneous)",
 "Bioengineering",
 "Catalysis",
 "Chemical Health and Safety",
 "Colloid and Surface Chemistry",
 "Filtration and Separation",
 "Fluid Flow and Transfer Processes",
 "Process Chemistry and Technology",
 "Chemistry(all)",
 "Chemistry (miscellaneous)",
 "Analytical Chemistry",
 "Electrochemistry",
 "Inorganic Chemistry",
 "Organic Chemistry",
 "Physical and Theoretical Chemistry",
 "Spectroscopy",
 "Computer Science(all)",
 "Computer Science (miscellaneous)",
 "Artificial Intelligence",
 "Computational Theory and Mathematics",
 "Computer Graphics and Computer-Aided Design",
 "Computer Networks and Communications",
 "Computer Science Applications",
 "Computer Vision and Pattern Recognition",
 "Hardware and Architecture",
 "Human-Computer Interaction",
 "Information Systems",
 "Signal Processing",
 "Software",
 "Decision Sciences(all)",
 "Decision Sciences (miscellaneous)",
 "Information Systems and Management",
 "Management Science and Operations Research",
 "Statistics, Probability and Uncertainty",
 "Earth and Planetary Sciences(all)",
 "Earth and Planetary Sciences (miscellaneous)",
 "Atmospheric Science",
 "Computers in Earth Sciences",
 "Earth-Surface Processes",
 "Economic Geology",
 "Geochemistry and Petrology",
 "Geology",
 "Geophysics",
 "Geotechnical Engineering and Engineering Geology",
 "Oceanography",
 "Palaeontology",
 "Space and Planetary Science",
 "Stratigraphy",
 "Economics, Econometrics and Finance(all)",
 "Economics, Econometrics and Finance (miscellaneous)",
 "Economics and Econometrics",
 "Finance",
 "Energy(all)",
 "Energy (miscellaneous)",
 "Energy Engineering and Power Technology",
 "Fuel Technology",
 "Nuclear Energy and Engineering",
 "Renewable Energy, Sustainability and the Environment",
 "Engineering(all)",
 "Engineering (miscellaneous)",
 "Aerospace Engineering",
 "Automotive Engineering",
 "Biomedical Engineering",
 "Civil and Structural Engineering",
 "Computational Mechanics",
 "Control and Systems Engineering",
 "Electrical and Electronic Engineering",
 "Industrial and Manufacturing Engineering",
 "Mechanical Engineering",
 "Mechanics of Materials",
 "Ocean Engineering",
 "Safety, Risk, Reliability and Quality",
 "Media Technology",
 "Building and Construction",
 "Architecture",
 "Environmental Science(all)",
 "Environmental Science (miscellaneous)",
 "Ecological Modelling",
 "Ecology",
 "Environmental Chemistry",
 "Environmental Engineering",
 "Global and Planetary Change",
 "Health, Toxicology and Mutagenesis",
 "Management, Monitoring, Policy and Law",
 "Nature and Landscape Conservation",
 "Pollution",
 "Waste Management and Disposal",
 "Water Science and Technology",
 "Immunology and Microbiology(all)",
 "Immunology and Microbiology (miscellaneous)",
 "Applied Microbiology and Biotechnology",
 "Immunology",
 "Microbiology",
 "Parasitology",
 "Virology",
 "Materials Science(all)",
 "Materials Science (miscellaneous)",
 "Biomaterials",
 "Ceramics and Composites",
 "Electronic, Optical and Magnetic Materials",
 "Materials Chemistry",
 "Metals and Alloys",
 "Polymers and Plastics",
 "Surfaces, Coatings and Films",
 "Mathematics(all)",
 "Mathematics (miscellaneous)",
 "Algebra and Number Theory",
 "Analysis",
 "Applied Mathematics",
 "Computational Mathematics",
 "Control and Optimization",
 "Discrete Mathematics and Combinatorics",
 "Geometry and Topology",
 "Logic",
 "Mathematical Physics",
 "Modelling and Simulation",
 "Numerical Analysis",
 "Statistics and Probability",
 "Theoretical Computer Science",
 "Medicine(all)",
 "Medicine (miscellaneous)",
 "Anatomy",
 "Anesthesiology and Pain Medicine",
 "Biochemistry, medical",
 "Cardiology and Cardiovascular Medicine",
 "Critical Care and Intensive Care Medicine",
 "Complementary and alternative medicine",
 "Dermatology",
 "Drug guides",
 "Embryology",
 "Emergency Medicine",
 "Endocrinology, Diabetes and Metabolism",
 "Epidemiology",
 "Family Practice",
 "Gastroenterology",
 "Genetics(clinical)",
 "Geriatrics and Gerontology",
 "Health Informatics",
 "Health Policy",
 "Hematology",
 "Hepatology",
 "Histology",
 "Immunology and Allergy",
 "Internal Medicine",
 "Infectious Diseases",
 "Microbiology (medical)",
 "Nephrology",
 "Clinical Neurology",
 "Obstetrics and Gynaecology",
 "Oncology",
 "Ophthalmology",
 "Orthopedics and Sports Medicine",
 "Otorhinolaryngology",
 "Pathology and Forensic Medicine",
 "Pediatrics, Perinatology, and Child Health",
 "Pharmacology (medical)",
 "Physiology (medical)",
 "Psychiatry and Mental health",
 "Public Health, Environmental and Occupational Health",
 "Pulmonary and Respiratory Medicine",
 "Radiology Nuclear Medicine and imaging",
 "Rehabilitation",
 "Reproductive Medicine",
 "Reviews and References, Medical",
 "Rheumatology",
 "Surgery",
 "Transplantation",
 "Urology",
 "Neuroscience(all)",
 "Neuroscience (miscellaneous)",
 "Behavioral Neuroscience",
 "Biological Psychiatry",
 "Cellular and Molecular Neuroscience",
 "Cognitive Neuroscience",
 "Developmental Neuroscience",
 "Endocrine and Autonomic Systems",
 "Neurology",
 "Sensory Systems",
 "Nursing(all)",
 "Nursing (miscellaneous)",
 "Advanced and Specialised Nursing",
 "Assessment and Diagnosis",
 "Care Planning",
 "Community and Home Care",
 "Critical Care",
 "Emergency",
 "Fundamentals and skills",
 "Gerontology",
 "Issues, ethics and legal aspects",
 "Leadership and Management",
 "LPN and LVN",
 "Maternity and Midwifery",
 "Medical–Surgical",
 "Nurse Assisting",
 "Nutrition and Dietetics",
 "Oncology(nursing)",
 "Pathophysiology",
 "Pediatrics",
 "Pharmacology (nursing)",
 "Phychiatric Mental Health",
 "Research and Theory",
 "Review and Exam Preparation",
 "Pharmacology, Toxicology and Pharmaceutics(all)",
 "Pharmacology, Toxicology and Pharmaceutics (miscellaneous)",
 "Drug Discovery",
 "Pharmaceutical Science",
 "Pharmacology",
 "Toxicology",
 "Physics and Astronomy(all)",
 "Physics and Astronomy (miscellaneous)",
 "Acoustics and Ultrasonics",
 "Astronomy and Astrophysics",
 "Condensed Matter Physics",
 "Instrumentation",
 "Nuclear and High Energy Physics",
 "Atomic and Molecular Physics, and Optics",
 "Radiation",
 "Statistical and Nonlinear Physics",
 "Surfaces and Interfaces",
 "Psychology(all)",
 "Psychology (miscellaneous)",
 "Applied Psychology",
 "Clinical Psychology",
 "Developmental and Educational Psychology",
 "Experimental and Cognitive Psychology",
 "Neuropsychology and Physiological Psychology",
 "Social Psychology",
 "Social Sciences(all)",
 "Social Sciences (miscellaneous)",
 "Archaeology",
 "Development",
 "Education",
 "Geography, Planning and Development",
 "Health(social science)",
 "Human Factors and Ergonomics",
 "Law",
 "Library and Information Sciences",
 "Linguistics and Language",
 "Safety Research",
 "Sociology and Political Science",
 "Transportation",
 "Anthropology",
 "Communication",
 "Cultural Studies",
 "Demography",
 "Gender Studies",
 "Life-span and Life-course Studies",
 "Political Science and International Relations",
 "Public Administration",
 "Urban Studies",
 "veterinary(all)",
 "veterinary (miscalleneous)",
 "Equine",
 "Food Animals",
 "Small Animals",
 "Dentistry(all)",
 "Dentistry (miscellaneous)",
 "Dental Assisting",
 "Dental Hygiene",
 "Oral Surgery",
 "Orthodontics",
 "Periodontics",
 "Health Professions(all)",
 "Health Professions (miscellaneous)",
 "Chiropractics",
 "Complementary and Manual Therapy",
 "Emergency Medical Services",
 "Health Information Management",
 "Medical Assisting and Transcription",
 "Medical Laboratory Technology",
 "Medical Terminology",
 "Occupational Therapy",
 "Optometry",
 "Pharmacy",
 "Physical Therapy, Sports Therapy and Rehabilitation",
 "Podiatry",
 "Radiological and Ultrasound Technology",
 "Respiratory Care",
 "Speech and Hearing",
    "Unknown"}; // unknown is always last entry - set indexToASJC by hand


    /**
     * Bottom level codes.
     * All ASJC codes are of form mmnn
     * where mm indicates mid level group.
     * The bottom index b is converted to ASJC using
     * indexToASJC[b]=mmnn
     * Unknown is always last entry and must be set by hand
     */
    public final static int [] indexToASJC = {
        1000,
    1100,
    1101,
    1102,
    1103,
    1104,
    1105,
    1106,
    1107,
    1108,
    1109,
    1110,
    1111,
    1200,
    1201,
    1202,
    1203,
    1204,
    1205,
    1206,
    1207,
    1208,
    1209,
    1210,
    1211,
    1212,
    1213,
    1300,
    1301,
    1302,
    1303,
    1304,
    1305,
    1306,
    1307,
    1308,
    1309,
    1310,
    1311,
    1312,
    1313,
    1314,
    1315,
    1400,
    1401,
    1402,
    1403,
    1404,
    1405,
    1406,
    1407,
    1408,
    1409,
    1410,
    1500,
    1501,
    1502,
    1503,
    1504,
    1505,
    1506,
    1507,
    1508,
    1600,
    1601,
    1602,
    1603,
    1604,
    1605,
    1606,
    1607,
    1700,
    1701,
    1702,
    1703,
    1704,
    1705,
    1706,
    1707,
    1708,
    1709,
    1710,
    1711,
    1712,
    1800,
    1801,
    1802,
    1803,
    1804,
    1900,
    1901,
    1902,
    1903,
    1904,
    1905,
    1906,
    1907,
    1908,
    1909,
    1910,
    1911,
    1912,
    1913,
    2000,
    2001,
    2002,
    2003,
    2100,
    2101,
    2102,
    2103,
    2104,
    2105,
    2200,
    2201,
    2202,
    2203,
    2204,
    2205,
    2206,
    2207,
    2208,
    2209,
    2210,
    2211,
    2212,
    2213,
    2214,
    2215,
    2216,
    2300,
    2301,
    2302,
    2303,
    2304,
    2305,
    2306,
    2307,
    2308,
    2309,
    2310,
    2311,
    2312,
    2400,
    2401,
    2402,
    2403,
    2404,
    2405,
    2406,
    2500,
    2501,
    2502,
    2503,
    2504,
    2505,
    2506,
    2507,
    2508,
    2600,
    2601,
    2602,
    2603,
    2604,
    2605,
    2606,
    2607,
    2608,
    2609,
    2610,
    2611,
    2612,
    2613,
    2614,
    2700,
    2701,
    2702,
    2703,
    2704,
    2705,
    2706,
    2707,
    2708,
    2709,
    2710,
    2711,
    2712,
    2713,
    2714,
    2715,
    2716,
    2717,
    2718,
    2719,
    2720,
    2721,
    2722,
    2723,
    2724,
    2725,
    2726,
    2727,
    2728,
    2729,
    2730,
    2731,
    2732,
    2733,
    2734,
    2735,
    2736,
    2737,
    2738,
    2739,
    2740,
    2741,
    2742,
    2743,
    2744,
    2745,
    2746,
    2747,
    2748,
    2800,
    2801,
    2802,
    2803,
    2804,
    2805,
    2806,
    2807,
    2808,
    2809,
    2900,
    2901,
    2902,
    2903,
    2904,
    2905,
    2906,
    2907,
    2908,
    2909,
    2910,
    2911,
    2912,
    2913,
    2914,
    2915,
    2916,
    2917,
    2918,
    2919,
    2920,
    2921,
    2922,
    2923,
    3000,
    3001,
    3002,
    3003,
    3004,
    3005,
    3100,
    3101,
    3102,
    3103,
    3104,
    3105,
    3106,
    3107,
    3108,
    3109,
    3110,
    3200,
    3201,
    3202,
    3203,
    3204,
    3205,
    3206,
    3207,
    3300,
    3301,
    3302,
    3303,
    3304,
    3305,
    3306,
    3307,
    3308,
    3309,
    3310,
    3311,
    3312,
    3313,
    3314,
    3315,
    3316,
    3317,
    3318,
    3319,
    3320,
    3321,
    3322,
    3400,
    3401,
    3402,
    3403,
    3404,
    3500,
    3501,
    3502,
    3503,
    3504,
    3505,
    3506,
    3600,
    3601,
    3602,
    3603,
    3604,
    3605,
    3606,
    3607,
    3608,
    3609,
    3610,
    3611,
    3612,
    3613,
    3614,
    3615,
    3616,
    3700}; // unknown is always last entry - set by hand must be mm00 
           // where mm is one more than penultimate entry

    /**
     * Bottom level index for unknown journals.
     * The unknown journal entries must be set by hand for most levels.
     * {@value}
     */
    final static int Unknown_BottomLevelIndex = indexToASJC.length-1;
    /**
     * ASJC for unknown journals.
     * The unknown journal entries must be set by hand for most levels.
     * {@value}
     */
    public final static int Unknown_ASJC=indexToASJC[Unknown_BottomLevelIndex];

    /**
     * ASJC to bottom index map.
     */
    static TreeMap<Integer,Integer> ASJCtoIndex;

    /**
     * Creates ASJC to bottom index map.
     */
    static public void createASJCtoBottomIndex(){
        ASJCtoIndex = new TreeMap();
        for (int b=0; b<indexToASJC.length; b++){
            ASJCtoIndex.put(indexToASJC[b], b);
        }
    }

   /**
    * Checks bottom level index
    * @param bottomLevel bottom level index
    * @return true if index is valid bottom level index.
    */
  static public boolean isbottomLevel(int bottomLevel){
    return ((bottomLevel>=0) && (bottomLevel<indexToASJC.length));
  }

   /**
    * Checks mid level index
    * @param midLevel mid level index
    * @return true if index is valid mid level index.
    */
  static public boolean isMidLevel(int midLevel){
    return ((midLevel>=0) && (midLevel<midLevelNames.length));
  }

   /**
    * Checks top level index
    * @param topLevel mid level index
    * @return true if index is valid top level index.
    */
  static public boolean isTopLevel(int topLevel){
    return ((topLevel>=0) && (topLevel<topLevelNames.length));
  }

   /**
    * Checks top level code to see if its the Unknown class
    * @param topLevel top level index
    * @return true if index is Unknown top level index.
    */
  static public boolean isUnknownTopLevel(int topLevel){
    return (topLevel==Unknown_TYPE);
  }

   /**
    * Checks mid level code to see if its the Unknown class
    * @param midLevel mid level index
    * @return true if index is Unknown mid level index.
    */
  static public boolean isUnknownMidLevel(int midLevel){
    return (midLevel==Unknown_MidLevelIndex);
  }

   /**
    * Checks bottom level code to see if its the Unknown class
    * @param bottomLevel bottom level index
    * @return true if index is Unknown bottom level index.
    */
  static public boolean isUnknownBottomLevel(int bottomLevel){
    return (bottomLevel==Unknown_BottomLevelIndex);
  }

   /**
    * Checks ASJC code to see if its the Unknown class
    * @param ASJC ASJC code
    * @return true if code is Unknown ASJC.
    */
  static public boolean isUnknownASJC(int ASJC){
    return (ASJC==Unknown_ASJC);
  }

   /**
    * Converts ASJC code to bottom level index.
    * @param ASJC
    * @return legal bottom level index.
    */
  static public int toBottomLevel(int ASJC){
      if (ASJCtoIndex==null) createASJCtoBottomIndex();
      Integer bottomLevel = ASJCtoIndex.get(ASJC);
      if (bottomLevel==null) return Unknown_BottomLevelIndex;
      return bottomLevel;
  }

   /**
    * Converts ASJC code to mid level code
    * @param ASJC
    * @return mid level index, may not be a legal one.
    */
  static public int toMidLevel(int ASJC){
    int midLevelIndex=(ASJC-1000)/100;
    if (isMidLevel(midLevelIndex)) return midLevelIndex;
    return Unknown_MidLevelIndex;
  }

   /**
    * Converts ASJC code to top level code
    * @param ASJC
    * @return top level index, -1 if invalid code
    */
  static public int toTopLevel(int ASJC){
    return midLevelToTopLevel(toMidLevel(ASJC));
  }
   /**
    * Converts mid level index to top level code
    * @param mid level index
    * @return top level index, (topLevelNames.length-1) i.e. unknown, if invalid code
    */
  static public int midLevelToTopLevel(int m){
    if (isMidLevel(m)) return midToTop[m];
    return Unknown_TYPE;
  }

  /**
   * Returns all ASJC codes associated with same mid level index.
   * That is codes have same first two digits.
   * @param ASJC ASJC code
   * @return all bottom level ASJC codes sharing same mid level.
   */
  public static ArrayList<Integer> midLevelList(int ASJC){
      return midLevelListFromIndex(toMidLevel(ASJC));
  }
  /**
   * Returns all ASJC codes associated with mid level index
   * @param m midLevel index
   * @return all bottom level ASJC codes.
   */
  public static ArrayList<Integer> midLevelListFromIndex(int m){
       if (!isMidLevel(m)) return null;
       ArrayList<Integer> midList = new ArrayList();
       for (int ASJC: ASJCclasses.indexToASJC){
            if (toMidLevel(ASJC)==m) midList.add(ASJC);
        }
       return midList;
  }

}
