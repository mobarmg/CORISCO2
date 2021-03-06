/**
 * $Id: $
 * $URL: $
 * *************************************************************************
 * Copyright (c) 2002-2009, DuraSpace.  All rights reserved
 * Licensed under the DuraSpace License.
 *
 * A copy of the DuraSpace License has been included in this
 * distribution and is available at: http://scm.dspace.org/svn/repo/licenses/LICENSE.txt
 */
package org.dspace.discovery;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.dspace.content.DSpaceObject;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

/**
 * User: mdiggory
 * Date: Feb 24, 2010
 * Time: 4:25:37 PM
 */
public class SearchUtils {

    private static final Logger log = Logger.getLogger(SearchUtils.class);

    private static ExtendedProperties props = null;

    private static Map<String, String[]> solrFacets = new HashMap<String, String[]>();

    private static Map<String, String[]> solrDateFacets = new HashMap<String, String[]>();

    static{
 
        //Method that will retrieve all the possible configs we have

        props = ExtendedProperties
                .convertProperties(ConfigurationManager.getProperties());

        try {
            File config = new File(props.getProperty("dspace.dir")
                    + "/config/dspace-solr-search.cfg");
            if (config.exists()) {
                props.combine(new ExtendedProperties(config.getAbsolutePath()));
            } else {
                ExtendedProperties defaults = new ExtendedProperties();
                defaults
                        .load(SolrServiceImpl.class
                                .getResourceAsStream("dspace-solr-search.cfg"));
                props.combine(defaults);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        try {
            Iterator allPropsIt = props.getKeys();
            while (allPropsIt.hasNext()) {
                String propName = String.valueOf(allPropsIt.next());
                if (propName.startsWith("solr.facets.")) {
                    String[] propVals = props.getStringArray(propName);

                     log.info("loading scope, " + propName);

                    List<String> facets = new ArrayList<String>();
                    List<String> dateFacets = new ArrayList<String>();
                    for (String propVal : propVals) {
                        if (propVal.endsWith("_dt"))  {
                            dateFacets.add(propVal.replace("_dt", ""));

                            log.info("value, " + propVal);

                        } else {
                            facets.add(propVal);

                            log.info("value, " + propVal);
                        }
                    }

                    //All the values are split into date & facetfields, so now store em
                    solrFacets.put(propName.replace("solr.facets.",""), facets.toArray(new String[facets.size()]));
                    solrDateFacets.put(propName.replace("solr.facets.",""), dateFacets.toArray(new String[dateFacets.size()]));

                    log.debug("solrFacets size: " + solrFacets.size());
                    log.debug("solrDateFacets size: " + solrDateFacets.size());
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public static ExtendedProperties getConfig() {

        return props;
    }

    public static String[] getFacetsForType(String type)
    {
        return solrFacets.get(type);
    }

    public static String[] getDateFacetsForType(String type)
    {
        return solrDateFacets.get(type);
    }

    public static DSpaceObject findDSpaceObject(Context context, SolrDocument doc) throws SQLException {

        Integer type = (Integer) doc.getFirstValue("search.resourcetype");
        Integer id = (Integer) doc.getFirstValue("search.resourceid");
        String handle = (String) doc.getFirstValue("handle");

        if (type != null && id != null) {
            return DSpaceObject.find(context, type, id);
        } else if (handle != null) {
            return HandleManager.resolveToObject(context, handle);
        }

        return null;
    }


    public static String[] getDefaultFilters(String scope){
        List<String> result = new ArrayList<String>();
        // Check (and add) any default filters which may be configured
        String defaultFilters = getConfig().getString("solr.default.filter");
        if(defaultFilters != null)
            result.addAll(Arrays.asList(defaultFilters.split(";")));

        if(scope != null){
            String scopeDefaultFilters = SearchUtils.getConfig().getString("solr." + scope + ".default.filter");
            if(scopeDefaultFilters != null)
                result.addAll(Arrays.asList(scopeDefaultFilters.split(";")));
        }
        return result.toArray(new String[result.size()]);
    }

}
