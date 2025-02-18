/*******************************************************************************
 * Copyright 2018-2020 Espressif Systems (Shanghai) PTE LTD. All rights reserved.
 * Use is subject to license terms.
 *******************************************************************************/
package com.espressif.idf.ui.size;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.json.simple.JSONObject;

import com.espressif.idf.core.logging.Logger;

/**
 * @author Kondal Kolipaka <kondal.kolipaka@espressif.com>
 *
 */
public class IDFSizeOverviewComposite
{

	private FormToolkit toolkit;
	private Composite overviewComp;
	private JSONObject overviewJson;
	private Font boldFont;
	private Composite chartComp;

	public void createPartControl(Composite parent, IFile file, String targetName)
	{
		toolkit = new FormToolkit(parent.getDisplay());
		Form form = toolkit.createForm(parent);
		toolkit.decorateFormHeading(form);
		form.setText(Messages.IDFSizeOverviewComposite_ApplicatoinMemoryUsage);
		form.getBody().setLayout(new GridLayout());

		Section ec2 = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		ec2.setText(Messages.IDFSizeOverviewComposite_Overview);
		ec2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		overviewComp = new Composite(ec2, SWT.NONE);
		overviewComp.setLayout(new GridLayout(2, false));
		overviewComp.setBackground(form.getBody().getBackground());
		overviewComp.setForeground(form.getBody().getForeground());
		ec2.setClient(overviewComp);

		overviewJson = getIDFSizeOverviewData(file, targetName);
		long dram_data = (long) overviewJson.get(IDFSizeConstants.DRAM_DATA);
		long dram_bss = (long) overviewJson.get(IDFSizeConstants.DRAM_BSS);
		long flash_code = (long) overviewJson.get(IDFSizeConstants.FLASH_CODE);
		long flash_rodata = (long) overviewJson.get(IDFSizeConstants.FLASH_RODATA);
		long total_size = (long) overviewJson.get(IDFSizeConstants.TOTAL_SIZE);

		Label sizeLbl = toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_TotalSize);
		Label sizeVal = toolkit.createLabel(overviewComp, convertToKB(total_size));

		FontDescriptor boldDescriptor = FontDescriptor.createFrom(sizeLbl.getFont()).setStyle(SWT.BOLD);
		boldFont = boldDescriptor.createFont(sizeLbl.getDisplay());
		sizeVal.setFont(boldFont);

		toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_DramDataSize);
		Label b1Val = toolkit.createLabel(overviewComp, convertToKB(dram_data));
		b1Val.setFont(boldFont);

		toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_DramBssSize);
		Label b2Val = toolkit.createLabel(overviewComp, convertToKB(dram_bss));
		b2Val.setFont(boldFont);

		toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_FlashCodeSize);
		Label b3Val = toolkit.createLabel(overviewComp, convertToKB(flash_code));
		b3Val.setFont(boldFont);

		toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_FlashRoDataSize);
		Label b4Val = toolkit.createLabel(overviewComp, convertToKB(flash_rodata));
		b4Val.setFont(boldFont);

		Section ec = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		ec.setText(Messages.IDFSizeOverviewComposite_MemoryAllocation);
		ec.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		chartComp = new Composite(ec, SWT.NONE);
		chartComp.setLayout(new GridLayout(2, false));
		chartComp.setBackground(form.getBody().getBackground());
		chartComp.setForeground(form.getBody().getForeground());
		ec.setClient(chartComp);

		// available_diram is non-zero for esp32
		long available_diram = overviewJson.get(IDFSizeConstants.AVAILABLE_DIRAM) != null
				? (long) overviewJson.get(IDFSizeConstants.AVAILABLE_DIRAM)
				: 0;
		if (available_diram == 0)
		{
			plotDoubleBar();
		}
		else
		{
			plotSingleBar();
		}

	}

	private void plotSingleBar()
	{
		// esps2-s2 specific
		long used_diram = (long) overviewJson.get(IDFSizeConstants.USED_DIRAM);
		long available_diram = (long) overviewJson.get(IDFSizeConstants.AVAILABLE_DIRAM);
		double used_diram_ratio = (double) overviewJson.get(IDFSizeConstants.USED_DIRAM_RATIO);

		toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_SinglePlot_UsedDiram);
		String chartText = String.format(Messages.IDFSizeOverviewComposite_UsedSizeText, convertToKB(used_diram),
				convertToKB(available_diram), Math.round(used_diram_ratio * 100));
		Label dramUsedVal = toolkit.createLabel(overviewComp, chartText); // $NON-NLS-1$
		dramUsedVal.setFont(boldFont);

		createChart(chartComp, used_diram, available_diram, chartText, "DIRAM"); //$NON-NLS-1$

	}

	protected void plotDoubleBar()
	{
		long used_iram = (long) overviewJson.get(IDFSizeConstants.USED_IRAM);
		long available_iram = (long) overviewJson.get(IDFSizeConstants.AVAILABLE_IRAM);
		double used_iram_ratio = (double) overviewJson.get(IDFSizeConstants.USED_IRAM_RATIO);

		long used_dram = (long) overviewJson.get(IDFSizeConstants.USED_DRAM);
		long available_dram = (long) overviewJson.get(IDFSizeConstants.AVAILABLE_DRAM);
		double used_dram_ratio = (double) overviewJson.get(IDFSizeConstants.USED_DRAM_RATIO);

		// Used static DRAM
		toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_UsedStaticDram);

		String dramText = String.format(Messages.IDFSizeOverviewComposite_UsedSizeText, convertToKB(used_dram),
				convertToKB(available_dram), Math.round(used_dram_ratio * 100));

		Label dramUsedVal = toolkit.createLabel(overviewComp, dramText); // $NON-NLS-1$
		dramUsedVal.setFont(boldFont);

		// Used static IRAM
		toolkit.createLabel(overviewComp, Messages.IDFSizeOverviewComposite_UsedStaticIram);
		String iramText = String.format(Messages.IDFSizeOverviewComposite_UsedSizeText, convertToKB(used_iram),
				convertToKB(available_iram), Math.round(used_iram_ratio * 100));
		Label iramUsedVal = toolkit.createLabel(overviewComp, iramText); // $NON-NLS-1$
		iramUsedVal.setFont(boldFont);

		createChart(chartComp, used_dram, available_dram, dramText, "DRAM"); //$NON-NLS-1$
		createChart(chartComp, used_iram, available_iram, iramText, "IRAM"); //$NON-NLS-1$

	}

	protected JSONObject getIDFSizeOverviewData(IFile file, String targetName)
	{
		// Get data
		JSONObject idfSizeOverview = null;
		try
		{
			idfSizeOverview = new IDFSizeDataManager().getIDFSizeOverview(file, targetName);
		}
		catch (Exception e)
		{
			Logger.log(e);
		}
		return idfSizeOverview;
	}

	/**
	 * create the chart.
	 * 
	 * @param parent        The parent composite
	 * @param available_ram
	 * @param used_ram
	 * @param chartText
	 * @param chartName
	 * @return The created chart
	 */
	static public Chart createChart(Composite parent, long used_ram, long available_ram, String chartText,
			String chartName)
	{

		double[] used = { used_ram / 1024 }; // KB
		double[] available = { available_ram / 1024 }; // KB

		// create a chart
		Chart chart = new Chart(parent, SWT.NONE);
		chart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// set titles
		chart.getTitle().setText("Used " + chartText); //$NON-NLS-1$

		chart.getAxisSet().getXAxis(0).getTitle().setText(""); //$NON-NLS-1$
		chart.getAxisSet().getYAxis(0).getTitle().setText(""); //$NON-NLS-1$ S

		// set category
		chart.getAxisSet().getXAxis(0).enableCategory(true);
		chart.getAxisSet().getXAxis(0).setCategorySeries(new String[] { chartName });

		// create bar series
		IBarSeries<?> barSeries1 = (IBarSeries<?>) chart.getSeriesSet().createSeries(SeriesType.BAR,
				Messages.IDFSizeOverviewComposite_ChartUsedText);
		barSeries1.setYSeries(used);
		barSeries1.setBarColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));

		IBarSeries<?> barSeries2 = (IBarSeries<?>) chart.getSeriesSet().createSeries(SeriesType.BAR,
				Messages.IDFSizeOverviewComposite_ChartAvailableText);
		barSeries2.setYSeries(available);
		barSeries2.setBarColor(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));

		// enable stack series
		barSeries1.enableStack(true);
		barSeries2.enableStack(true);
		chart.setSize(100, 200);

		// adjust the axis range
		chart.getAxisSet().adjustRange();
		return chart;
	}

	protected String convertToKB(long value)
	{
		return String.valueOf(Math.round(value / 1024)) + " KB"; //$NON-NLS-1$
	}

}
