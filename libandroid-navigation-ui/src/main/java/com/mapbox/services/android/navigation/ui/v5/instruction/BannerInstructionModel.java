package com.mapbox.services.android.navigation.ui.v5.instruction;

import android.content.Context;

import com.mapbox.api.directions.v5.models.BannerText;
import com.mapbox.services.android.navigation.v5.milestone.BannerInstructionMilestone;
import com.mapbox.services.android.navigation.v5.navigation.NavigationUnitType;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import java.util.Locale;

public class BannerInstructionModel extends InstructionModel {

  private BannerText primaryBannerText;
  private BannerText secondaryBannerText;

  public BannerInstructionModel(Context context, BannerInstructionMilestone milestone,
                                RouteProgress progress, Locale locale, @NavigationUnitType.UnitType int unitType) {
    super(context, progress, locale, unitType);
    primaryBannerText = milestone.getBannerInstructions().primary();
    secondaryBannerText = milestone.getBannerInstructions().secondary();
  }

  @Override
  BannerText getPrimaryBannerText() {
    return primaryBannerText;
  }

  @Override
  BannerText getSecondaryBannerText() {
    return secondaryBannerText;
  }

  @Override
  String getManeuverType() {
    return primaryBannerText.type();
  }

  @Override
  String getManeuverModifier() {
    return primaryBannerText.modifier();
  }
}
