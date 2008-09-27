package com.xpn.xwiki.plugin.skinx;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;

public class CssSkinExtensionPlugin extends SkinExtensionPlugin
{
    /** Log object to log messages in this class. */
    private static final Log LOG = LogFactory.getLog(CssSkinExtensionPlugin.class);

    public static final String SSX_CLASS_NAME = "XWiki.StyleSheetExtension";

    public CssSkinExtensionPlugin(String name, String className, XWikiContext context)
    {
        super(name, className, context);
        init(context);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.plugin.XWikiDefaultPlugin#getName()
     */
    @Override
    public String getName()
    {
        return "ssx";
    }

    /**
     * @see com.xpn.xwiki.plugin.XWikiDefaultPlugin#init(com.xpn.xwiki.XWikiContext)
     */
    @Override
    public void init(XWikiContext context)
    {
        super.init(context);
        getSsxClass(context);
    }

    /**
     * @see com.xpn.xwiki.plugin.XWikiDefaultPlugin#virtualInit(com.xpn.xwiki.XWikiContext)
     */
    @Override
    public void virtualInit(XWikiContext context)
    {
        super.virtualInit(context);
        getSsxClass(context);
    }

    /**
     * {@inheritDoc}
     * 
     * @see SkinExtensionPlugin#getLink(String, XWikiContext)
     */
    @Override
    public String getLink(String documentName, XWikiContext context)
    {
        try {
            return "<link rel='stylesheet' type='text/css' href='"
                + context.getWiki().getURL(documentName, "ssx", context) + "'/>";
        } catch (XWikiException e) {
            LOG.warn("Cannot link to CSS extension: " + documentName);
            return "";
        }
    }

    @Override
    public void beginParsing(XWikiContext context)
    {
        super.beginParsing(context);
    }

    @Override
    public String endParsing(String content, XWikiContext context)
    {
        return super.endParsing(content, context);
    }

    public BaseClass getSsxClass(XWikiContext context)
    {
        try {
            XWikiDocument doc = context.getWiki().getDocument(SSX_CLASS_NAME, context);
            boolean needsUpdate = false;

            BaseClass bclass = doc.getxWikiClass();
            if (context.get("initdone") != null) {
                return bclass;
            }

            bclass.setName(SSX_CLASS_NAME);

            needsUpdate |= bclass.addTextField("name", "Name", 30);
            needsUpdate |= bclass.addTextAreaField("code", "Code", 50, 20);
            needsUpdate |= bclass.addStaticListField("use", "Use this extension", "onDemand=On demand|always=Always");
            needsUpdate |= bclass.addBooleanField("parse", "Parse content", "yesno");
            needsUpdate |= bclass.addStaticListField("cache", "Caching policy", "long|short|default|forbid");

            if (StringUtils.isBlank(doc.getAuthor())) {
                needsUpdate = true;
                doc.setAuthor("XWiki.Admin");
            }
            if (StringUtils.isBlank(doc.getCreator())) {
                needsUpdate = true;
                doc.setCreator("XWiki.Admin");
            }
            if (StringUtils.isBlank(doc.getParent())) {
                needsUpdate = true;
                doc.setParent("XWiki.XWikiClasses");
            }
            if (StringUtils.isBlank(doc.getContent())) {
                needsUpdate = true;
                doc.setContent("1 XWiki Stylesheet Extension Class");
            }

            if (needsUpdate) {
                context.getWiki().saveDocument(doc, context);
            }
            return bclass;
        } catch (Exception ex) {
            LOG.error("Cannot initialize SsxClass", ex);
        }
        return null;
    }
}
