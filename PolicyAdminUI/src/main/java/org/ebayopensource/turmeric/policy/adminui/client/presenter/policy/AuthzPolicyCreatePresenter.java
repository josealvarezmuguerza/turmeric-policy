/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.policy.adminui.client.presenter.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.policy.adminui.client.PolicyAdminUIUtil;
import org.ebayopensource.turmeric.policy.adminui.client.SupportedService;
import org.ebayopensource.turmeric.policy.adminui.client.model.PolicyAdminUIService;
import org.ebayopensource.turmeric.policy.adminui.client.model.HistoryToken;
import org.ebayopensource.turmeric.policy.adminui.client.model.policy.GenericPolicy;
import org.ebayopensource.turmeric.policy.adminui.client.model.policy.PolicyQueryService.ResourceLevel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class AuthzPolicyCreatePresenter.
 */
public class AuthzPolicyCreatePresenter extends PolicyCreatePresenter {

	/**
	 * Instantiates a new authz policy create presenter.
	 * 
	 * @param eventBus
	 *            the event bus
	 * @param view
	 *            the view
	 * @param serviceMap
	 *            the service map
	 */
	public AuthzPolicyCreatePresenter(final HandlerManager eventBus,
			final PolicyCreateDisplay view,
			final Map<SupportedService, PolicyAdminUIService> serviceMap) {
		super(eventBus, view, serviceMap);
		view.setConditionBuilderVisible(false);
		this.view.setPolicyType("AUTHZ");
		view.setExclusionListsVisible(false);
	}

	/** The Constant PRESENTER_ID. */
	public static final String PRESENTER_ID = "AuthzPolicyCreate";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ebayopensource.turmeric.policy.adminui.client.presenter.Presenter
	 * #getId()
	 */
	@Override
	public final String getId() {
		return PRESENTER_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.policy.adminui.client.presenter.policy.
	 * PolicyCreatePresenter#getResourceLevels()
	 */
	@Override
	public final List<String> getResourceLevels() {
		List<String> rsLevels = new ArrayList<String>();
		rsLevels.add(ResourceLevel.OPERATION.name());
		return rsLevels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.policy.adminui.client.presenter.policy.
	 * PolicyCreatePresenter#bindSaveButton()
	 */
	@Override
	protected final void bindSaveButton() {

		// fired on saved policy
		this.view.addSaveButtonClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {

				final GenericPolicy p = getPolicy(view.getPolicyName()
						.getValue(), "AUTHZ", view.getPolicyDesc().getValue(),
						resourceAssignments, view.getSubjectContentView()
								.getAssignments(), null);

				/**
				 * This timer is needed due to GWT has only one thread, so
				 * Thread.sleep is not a valid option The purpose of sleeping
				 * time is wait until new external subject been created into
				 * turmeric db, in order to assign them as internal subjects
				 */
				Timer timer = new Timer() {
					public void run() {

						service.createPolicy(
								p,
								new AsyncCallback<org.ebayopensource.turmeric.policy.adminui.client.model.policy.PolicyQueryService.CreatePolicyResponse>() {

									public void onFailure(Throwable err) {
										if (err.getLocalizedMessage().contains(
												"500")) {
											view.getResourceContentView()
													.error(PolicyAdminUIUtil.messages
															.serverError(PolicyAdminUIUtil.policyAdminConstants
																	.genericErrorMessage()));
										} else {
											view.getResourceContentView()
													.error(PolicyAdminUIUtil.messages.serverError(err
															.getLocalizedMessage()));
										}
									}

									public void onSuccess(
											final org.ebayopensource.turmeric.policy.adminui.client.model.policy.PolicyQueryService.CreatePolicyResponse response) {

										AuthzPolicyCreatePresenter.this.view
												.clear();
										clearLists();
										HistoryToken token = makeToken(
												PolicyController.PRESENTER_ID,
												PolicySummaryPresenter.PRESENTER_ID,
												null);
										History.newItem(token.toString(), true);
									}
								});
						view.getSaveButton().setEnabled(true);
					}

				};
				if (view.getSubjectContentView().getAssignments().size() > 0
						&& "USER".equals(view.getSubjectContentView()
								.getAssignments().get(0).getSubjectType())) {
					view.getSaveButton().setEnabled(false);
					timer.schedule(3000);
				} else {
					timer.schedule(1);
				}

			}
		});

	}
}
