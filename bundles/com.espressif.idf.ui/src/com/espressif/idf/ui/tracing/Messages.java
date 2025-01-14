/*******************************************************************************
 * Copyright 2021 Espressif Systems (Shanghai) PTE LTD. All rights reserved.
 * Use is subject to license terms.
 *******************************************************************************/

package com.espressif.idf.ui.tracing;

import org.eclipse.osgi.util.NLS;

/**
 * Messages class to fetch language based properties in tracing components
 * 
 * @author Ali Azam Rana
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.espressif.idf.ui.tracing.messages"; //$NON-NLS-1$

	public static String TracingAnalysisEditor_OverviewTab;
	public static String TracingAnalysisEditor_OverviewFromHeading;
	public static String TracingAnalysisEditor_OverviewChartSectionHeading;
	public static String TracingAnalysisEditor_OverviewDetailSectionHeading;
	public static String TracingAnalysisEditor_OverviewDetailSectionEnableLogScaleButtonText;
	public static String TracingAnalysisEditor_OverviewDetailSectionListAvailableEvents;
	public static String TracingAnalysisEditor_OverviewDetailSectionListSelectedEvents;
	public static String TracingAnalysisEditor_OverviewChartSectionMultiChartHeading;
	public static String TracingAnalysisEditor_OverviewChartSectionYAxisTitle;
	public static String TracingAnalysisEditor_OverviewChartSectionHeapChartHeading;
	public static String TracingAnalysisEditor_OverviewDetailSectionAllContexts;

	public static String TracingAnalysisEditor_DetailsColAddress;
	public static String TracingAnalysisEditor_DetailsColCoreId;
	public static String TracingAnalysisEditor_DetailsColContextName;
	public static String TracingAnalysisEditor_DetailsColEventId;
	public static String TracingAnalysisEditor_DetailsColIsIrq;
	public static String TracingAnalysisEditor_DetailsColCallers;
	public static String TracingAnalysisEditor_DetailsColSizeOfAllocatedBlock;
	public static String TracingAnalysisEditor_DetailsColTimestamp;
	public static String TracingAnalysisEditor_DetailsColDetail;
	public static String TracingAnalysisEditor_DetailsEventName;
	public static String TracingAnalysisEditor_DetailsStreamName;
	public static String TracingAnalysisEditor_DetailsContextMenuShowCallers;
	public static String TracingAnalysisEditor_DetailsContextMenuShowCallersTooltip;

	public static String TracingCallerView_ColFileName;
	public static String TracingCallerView_ColFunctionName;
	public static String TracingCallerView_ColLineNumber;
	public static String TracingCallerView_ColAddress;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
