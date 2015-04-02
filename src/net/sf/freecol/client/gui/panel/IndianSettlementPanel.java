/**
 *  Copyright (C) 2002-2015   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.client.gui.panel;

import java.util.logging.Logger;
import javax.swing.ImageIcon;

import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.common.i18n.Messages;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.resources.ResourceManager;


/**
 * This panel is used to show information about an Indian settlement.
 */
public final class IndianSettlementPanel extends FreeColPanel {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(IndianSettlementPanel.class.getName());


    /**
     * Creates a panel to show information about a native settlement.
     *
     * @param freeColClient The <code>FreeColClient</code> for the game.
     * @param settlement The <code>IndianSettlement</code> to display.
     */
    public IndianSettlementPanel(FreeColClient freeColClient,
                                 IndianSettlement settlement) {
        super(freeColClient, new MigLayout("wrap 2, gapx 20", "", ""));

        ImageLibrary imageLibrary = getLibrary();
        JLabel settlementLabel = new JLabel(new ImageIcon(
            imageLibrary.getSettlementImage(settlement)));
        Player indian = settlement.getOwner();
        Player player = getMyPlayer();
        boolean contacted = settlement.hasContacted(player);
        boolean visited = settlement.hasVisited(player);
        String text = Messages.message(settlement.getLocationLabelFor(player))
            + ", "
            + Messages.message(StringTemplate
                .template(settlement.isCapital() ? "indianCapital"
                    : "indianSettlement")
                .addStringTemplate("%nation%", indian.getNationName()));
        String messageId = settlement.getShortAlarmLevelMessageId(player);
        text += " (" + Messages.message(messageId);
        if (settlement.worthScouting(player)) {
            text += "," + ResourceManager.getString("unscoutedIndianSettlement");
        }
        text += ")";
        settlementLabel.setText(text);
        add(settlementLabel);

        final Unit missionary = settlement.getMissionary();
        if (missionary != null) {
            add(GUI.localizedLabel(missionary.getLabel(Unit.UnitLabelType.NATIONAL),
                    new ImageIcon(imageLibrary.getSmallObjectImage(missionary)),
                    JLabel.CENTER));
        }

        add(GUI.localizedLabel("indianSettlement.learnableSkill"), "newline");
        final UnitType skillType = settlement.getLearnableSkill();
        add(GUI.localizedLabel(settlement.getLearnableSkillLabel(visited),
                ((visited && skillType != null)
                    ? new ImageIcon(imageLibrary.getSmallObjectImage(skillType))
                    : null),
                JLabel.CENTER));

        add(GUI.localizedLabel("indianSettlement.mostHated"), "newline");
        final Player mostHated = settlement.getMostHated();
        add(GUI.localizedLabel(settlement.getMostHatedLabel(contacted),
                ((contacted && mostHated != null)
                    ? new ImageIcon(imageLibrary.getSmallObjectImage(mostHated))
                    : null),
                JLabel.CENTER));

        GoodsType[] wantedGoods = settlement.getWantedGoods();
        final int n = (visited) ? settlement.getWantedGoodsAmount() : 2;
        add(GUI.localizedLabel("indianSettlement.highlyWanted"), "newline");
        add(GUI.localizedLabel(settlement.getWantedGoodsLabel(0, player),
                ((visited && wantedGoods[0] != null)
                    ? new ImageIcon(imageLibrary.getGoodsImage(wantedGoods[0]))
                    : null),
                JLabel.CENTER));
        add(GUI.localizedLabel("indianSettlement.otherWanted"), "newline");
        String x = "split " + Integer.toString(n-1);
        for (int i = 1; i < n; i++) {
            add(GUI.localizedLabel(settlement.getWantedGoodsLabel(i, player),
                    ((visited && wantedGoods[i] != null)
                        ? new ImageIcon(imageLibrary.getGoodsImage(wantedGoods[i]))
                        : null),
                    JLabel.CENTER),
                x);
            x = null;
        }

        add(okButton, "newline 20, span, tag ok");

        setSize(getPreferredSize());
    }
}
