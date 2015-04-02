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

package net.sf.freecol.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.JComponent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.common.i18n.Messages;
import net.sf.freecol.common.model.Ability;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FreeColGameObjectType;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.LostCityRumour;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.ResourceType;
import net.sf.freecol.common.model.Role;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.SettlementType;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovementStyle;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.resources.ImageResource;
import net.sf.freecol.common.resources.ResourceManager;


/**
 * Holds various images that can be called upon by others in order to display
 * certain things.
 */
public final class ImageLibrary {

    private static final Logger logger = Logger.getLogger(ImageLibrary.class.getName());

    public static final String UNIT_SELECT = "unitSelect.image",
                               DELETE = "delete.image",
                               PLOWED = "model.improvement.plow.image",
                               TILE_TAKEN = "tileTaken.image",
                               TILE_OWNED_BY_INDIANS = "nativeLand.image",
                               LOST_CITY_RUMOUR = "lostCityRumour.image",
                               DARKNESS = "halo.dark.image";

    public static enum PathType {
        NAVAL,
        WAGON,
        HORSE,
        FOOT;

        /**
         * Get a message key for this path type.
         *
         * @return A message key.
         */
        public String getKey() {
            return toString().toLowerCase(Locale.US);
        }

        public String getImageKey() {
            return "path." + getKey() + ".image";
        }

        public String getNextTurnImageKey() {
            return "path." + getKey() + ".nextTurn.image";
        }

        /**
         * Get the broad class of image to show along unit paths.
         *
         * @param u A <code>Unit</code> to classify.
         * @return A suitable <code>PathType</code>.
         */
        public static PathType getPathType(Unit u) {
            return (u == null) ? PathType.FOOT
                : (u.isNaval()) ? PathType.NAVAL
                : (u.isMounted()) ? PathType.HORSE
                : (u.isPerson()) ? PathType.FOOT
                : PathType.WAGON;
        }
    };


    /**
     * The scaling factor used when creating this
     * <code>ImageLibrary</code>.  The value <code>1</code> is used if
     * this object is not a result of a scaling operation.
     */
    private final float scalingFactor;

    private final Map<String, Integer> imageCounts = new HashMap<>();


    /**
     * The constructor to use when needing an unscaled <code>ImageLibrary</code>.
     */
    public ImageLibrary() {
        this(1f);
    }

    /**
     * The constructor to use when needing a scaled <code>ImageLibrary</code>.
     * @param scalingFactor The factor used when scaling. 2 is twice
     *      the size of the original images and 0.5 is half.
     */
    public ImageLibrary(float scalingFactor) {
        this.scalingFactor = scalingFactor;
    }


    /**
     * Returns the scaling factor used when creating this <code>ImageLibrary</code>.
     * It is 1 if the constructor without scaling factor was used to create
     * this object.
     * @return The scaling factor of this ImageLibrary.
     */
    public float getScalingFactor() {
        return scalingFactor;
    }

    /**
     * Create a "chip" with the given text and colors.
     *
     * @param text The text to display.
     * @param border The border <code>Color</code>.
     * @param background The background <code>Color</code>.
     * @param foreground The foreground <code>Color</code>.
     * @return A chip.
     */
    private Image createChip(String text, Color border,
                             Color background, Color foreground) {
        // Draw it and put it in the cache
        Font font = FontLibrary.createFont(FontLibrary.FontType.SIMPLE, FontLibrary.FontSize.TINY, Font.BOLD, scalingFactor);
        // hopefully, this is big enough
        BufferedImage bi = new BufferedImage(100, 100,
                                             BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        TextLayout label = new TextLayout(text, font,
                                          g2.getFontRenderContext());
        float padding = 6 * scalingFactor;
        int width = (int)(label.getBounds().getWidth() + padding);
        int height = (int)(label.getAscent() + label.getDescent() + padding);
        g2.setColor(border);
        g2.fillRect(0, 0, width, height);
        g2.setColor(background);
        g2.fillRect(1, 1, width - 2, height - 2);
        g2.setColor(foreground);
        label.draw(g2, (float)(padding/2 - label.getBounds().getX()),
                   label.getAscent() + padding/2);
        g2.dispose();
        return bi.getSubimage(0, 0, width, height);
    }

    /**
     * Create a filled "chip" with the given text and colors.
     *
     * @param text The text to display.
     * @param border The border <code>Color</code>.
     * @param background The background <code>Color</code>.
     * @param amount How much to fill the chip with the fill color
     * @param fill The fill <code>Color</code>.
     * @param foreground The foreground <code>Color</code>.
     * @return A chip.
     */
    private Image createFilledChip(String text, Color border, Color background,
                                   double amount, Color fill,
                                   Color foreground) {
        // Draw it and put it in the cache
        Font font = FontLibrary.createFont(FontLibrary.FontType.SIMPLE, FontLibrary.FontSize.TINY, Font.BOLD, scalingFactor);
        // hopefully, this is big enough
        BufferedImage bi = new BufferedImage(100, 100,
                                             BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        TextLayout label = new TextLayout(text, font,
                                          g2.getFontRenderContext());
        float padding = 6 * scalingFactor;
        int width = (int)(label.getBounds().getWidth() + padding);
        int height = (int)(label.getAscent() + label.getDescent() + padding);
        g2.setColor(border);
        g2.fillRect(0, 0, width, height);
        g2.setColor(background);
        g2.fillRect(1, 1, width - 2, height - 2);
        if (amount > 0.0 && amount <= 1.0) {
            g2.setColor(fill);
            g2.fillRect(1, 1, width - 2, (int)((height - 2) * amount));
        }
        g2.setColor(foreground);
        label.draw(g2, (float)(padding/2 - label.getBounds().getX()),
                   label.getAscent() + padding/2);
        g2.dispose();
        return bi.getSubimage(0, 0, width, height);
    }

    /**
     * Create a faded version of an image.
     *
     * @param img The <code>Image</code> to fade.
     * @param fade The amount of fading.
     * @param target The offset.
     * @return The faded image.
     */
    public static BufferedImage fadeImage(Image img, float fade, float target) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(img, 0, 0, null);

        float offset = target * (1.0f - fade);
        float[] scales = { fade, fade, fade, 1.0f };
        float[] offsets = { offset, offset, offset, 0.0f };
        RescaleOp rop = new RescaleOp(scales, offsets, null);

        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(bi, rop, 0, 0);

        g.dispose();
        return bi;
    }

    public static BufferedImage createResizedImage(Image image,
                                                  int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return scaled;
    }

    /**
     * Gets a suitable foreground color given a background color.
     *
     * Our eyes have different sensitivity towards red, green and
     * blue.  We want a foreground color with the inverse brightness.
     *
     * @param background The background <code>Color</code> to complement.
     * @return A suitable foreground <code>Color</code>.
     */
    public static Color getForegroundColor(Color background) {
        return (background == null
            || (background.getRed() * 0.3 + background.getGreen() * 0.59
                + background.getBlue() * 0.11 >= 126))
            ? Color.BLACK
            : Color.WHITE;
    }

    /**
     * Describe <code>getStringBorderColor</code> method here.
     *
     * The string border colors should be black unless the color of
     * the string is really dark.
     *
     * @param color The <code>Color</code> to complement.
     * @return A suitable border <code>Color</code>.
     */
    public static Color getStringBorderColor(Color color) {
        return (color.getRed() * 0.3
            + color.getGreen() * 0.59
            + color.getBlue() * 0.11 < 10) ? Color.WHITE
            : Color.BLACK;
    }

    /**
     * Draw a (usually small) background image into a (usually larger)
     * space specified by a component, tiling the image to fill up the
     * space.  If the image is not available, just fill with the background
     * colour.
     *
     * @param resource The name of the <code>ImageResource</code> to tile with.
     * @param g The <code>Graphics</code> to draw to.
     * @param c The <code>JComponent</code> that defines the space.
     * @param insets Optional <code>Insets</code> to apply.
     */
    public static void drawTiledImage(String resource, Graphics g,
                                      JComponent c, Insets insets) {
        int width = c.getWidth();
        int height = c.getHeight();
        Image image = ResourceManager.getImage(resource);
        int dx, dy, xmin, ymin;

        if (insets == null) {
            xmin = 0;
            ymin = 0;
        } else {
            xmin = insets.left;
            ymin = insets.top;
            width -= insets.left + insets.right;
            height -= insets.top + insets.bottom;
        }
        if (image != null && (dx = image.getWidth(null)) > 0
            && (dy = image.getHeight(null)) > 0) {
            int xmax, ymax;
            xmax = xmin + width;
            ymax = ymin + height;
            for (int x = xmin; x < xmax; x += dx) {
                for (int y = ymin; y < ymax; y += dy) {
                    g.drawImage(image, x, y, null);
                }
            }
        } else {
            g.setColor(c.getBackground());
            g.fillRect(xmin, ymin, width, height);
        }
    }


    /**
     * Gets a chip image for the alarm at an Indian settlement.
     * The background is either the native owner's, or that of the
     * most hated nation if any.
     *
     * @param is The <code>IndianSettlement</code> to check.
     * @param player The observing <code>Player</code>.
     * @return An alarm chip, or null if none suitable.
     */
    public Image getAlarmChip(IndianSettlement is, Player player) {
        if (player == null || !is.hasContacted(player)) return null;
        Color ownerColor = is.getOwner().getNationColor();
        Player enemy = is.getMostHated();
        Color enemyColor = (enemy == null) ? Nation.UNKNOWN_NATION_COLOR
            : enemy.getNationColor();
        // Set amount to [0-4] corresponding to HAPPY, CONTENT,
        // DISPLEASED, ANGRY, HATEFUL but only if the player is the
        // most hated, because other nation alarm is not nor should be
        // serialized to the client.
        int amount = 4;
        if (enemy == null) {
            amount = 0;
        } else if (player == enemy) {
            Tension alarm = is.getAlarm(enemy);
            amount = (alarm == null) ? 4 : alarm.getLevel().ordinal();
            if (amount == 0) amount = 1; // Show *something*!
        }
        Color foreground = getForegroundColor(enemyColor);
        String text = ResourceManager.getString((is.worthScouting(player))
                                       ? "indianAlarmChip.contacted"
                                       : "indianAlarmChip.scouted");
        String key = "dynamic.alarm." + text + "." + ownerColor.getRGB()
            + "." + amount + "." + enemyColor.getRGB()
            + "." + Float.toHexString(scalingFactor);
        Image img = ResourceManager.getImage(key);
        if (img == null) {
            img = createFilledChip(text, Color.BLACK, ownerColor, amount/4.0,
                                   enemyColor, foreground);
            ResourceManager.addGameMapping(key, new ImageResource(img));
        }
        return img;
    }

    /**
     * Returns true if the tile with the given coordinates is to be
     * considered "even".  This is useful to select different images
     * for the same tile type in order to prevent big stripes or a
     * checker-board effect.
     *
     * @param x an <code>int</code> value
     * @param y an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    private static boolean isEven(int x, int y) {
        return ((y % 8 <= 2) || ((x + y) % 2 == 0 ));
    }

    /**
     * Returns the beach corner image at the given index.
     *
     * @param index The index of the image to return.
     * @param x an <code>int</code> value
     * @param y an <code>int</code> value
     * @return The image at the given index.
     */
    public Image getBeachCornerImage(int index, int x, int y) {
        return ResourceManager.getImage("model.tile.beach.corner" + index
                                        + ((isEven(x, y)) ? "_even" : "_odd"),
                                        scalingFactor);
    }

    /**
     * Returns the beach edge image at the given index.
     *
     * @param index The index of the image to return.
     * @param x an <code>int</code> value
     * @param y an <code>int</code> value
     * @return The image at the given index.
     */
    public Image getBeachEdgeImage(int index, int x, int y) {
        return ResourceManager.getImage("model.tile.beach.edge" + index
                                        + ((isEven(x, y)) ? "_even" : "_odd"),
                                        scalingFactor);
    }

    /**
     * Gets the bonus image of the given type.
     *
     * @param type The <code>ResourceType</code> to look up.
     * @return The bonus image.
     */
    public Image getBonusImage(ResourceType type) {
        return getBonusImage(type, scalingFactor);
    }

    public static Image getBonusImage(ResourceType type, float scale) {
        return ResourceManager.getImage(type.getId() + ".image", scale);
    }

    /**
     * Returns the border terrain-image for the given type.
     *
     * @param type The type of the terrain-image to return.
     * @param direction a <code>Direction</code> value
     * @param x The x-coordinate of the location of the tile that is being
     *     drawn.
     * @param y The x-coordinate of the location of the tile that is being
     *     drawn.
     * @return The terrain-image at the given index.
     */
    public Image getBorderImage(TileType type, Direction direction,
                                int x, int y) {
        String key = (type == null) ? "model.tile.unexplored" : type.getId();
        return ResourceManager.getImage(key + ".border_" + direction
                                        + ((isEven(x, y)) ?  "_even" : "_odd")
                                        + ".image", scalingFactor);
    }

    /**
     * Returns the coat-of-arms image for the given Nation.
     *
     * @param nation The nation.
     * @return the coat-of-arms of this nation
     */
    public Image getCoatOfArmsImage(Nation nation) {
        return getCoatOfArmsImage(nation, scalingFactor);
    }

    public static Image getCoatOfArmsImage(Nation nation, float scale) {
        return ResourceManager.getImage(nation.getId() + ".image", scale);
    }

    public static Image getCoatOfArmsImage(Nation nation, Dimension size) {
        return ResourceManager.getImage(nation.getId() + ".image", size);
    }

    /**
     * Returns the scaled terrain-image for a terrain type (and position 0, 0).
     *
     * @param type The type of the terrain-image to return.
     * @param scale The scale of the terrain image to return.
     * @return The terrain-image
     */
    public Image getCompoundTerrainImage(TileType type, float scale) {
        // Currently used for hills and mountains
        Image terrainImage = getTerrainImage(type, 0, 0, scale);
        Image overlayImage = getOverlayImage(type, type.getId(), scale);
        Image forestImage = type.isForested() ? getForestImage(type, scale)
            : null;
        if (overlayImage == null && forestImage == null) {
            return terrainImage;
        } else {
            GraphicsConfiguration gc
                = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            int width = terrainImage.getWidth(null);
            int height = terrainImage.getHeight(null);
            if (overlayImage != null) {
                height = Math.max(height, overlayImage.getHeight(null));
            }
            if (forestImage != null) {
                height = Math.max(height, forestImage.getHeight(null));
            }
            BufferedImage compositeImage
                = gc.createCompatibleImage(width, height,
                                           Transparency.TRANSLUCENT);
            Graphics2D g = compositeImage.createGraphics();
            g.drawImage(terrainImage, 0,
                        height - terrainImage.getHeight(null), null);
            if (overlayImage != null) {
                g.drawImage(overlayImage, 0,
                            height - overlayImage.getHeight(null), null);
            }
            if (forestImage != null) {
                g.drawImage(forestImage, 0,
                            height - forestImage.getHeight(null), null);
            }
            g.dispose();
            return compositeImage;
        }
    }

    /**
     * Returns the height of the terrain-image including overlays and
     * forests for the given terrain type.
     *
     * @param type The type of the terrain-image.
     * @return The height of the terrain-image at the given index.
     */
    public int getCompoundTerrainImageHeight(TileType type) {
        // TODO: Simplify method to not uselessly create images
        Image terrain = getTerrainImage(type, 0, 0);
        int height = terrain.getHeight(null);
        if (type != null) {
            Image overlayImage = getOverlayImage(type, type.getId(), scalingFactor);
            if (overlayImage != null) {
                height = Math.max(height, overlayImage.getHeight(null));
            }
            if (type.isForested()) {
                height = Math.max(height, getForestImage(type).getHeight(null));
            }
        }
        return height;
    }

    /**
     * Returns the forest image for a terrain type.
     *
     * @param type The type of the terrain-image to return.
     * @return The image at the given index.
     */
    public Image getForestImage(TileType type) {
        return getForestImage(type, scalingFactor);
    }

    public Image getForestImage(TileType type, TileImprovementStyle riverStyle) {
        return getForestImage(type, riverStyle, scalingFactor);
    }

    public static Image getForestImage(TileType type, float scale) {
        return ResourceManager.getImage(type.getId() + ".forest", scale);
    }

    public static Image getForestImage(TileType type, TileImprovementStyle riverStyle, float scale) {
        if (riverStyle == null) {
            return ResourceManager.getImage(type.getId() + ".forest", scale);
        } else {
            return ResourceManager.getImage(type.getId() + ".forest" + riverStyle.getMask(), scale);
        }
    }

    /**
     * Returns the portrait of this Founding Father.
     *
     * @param father a <code>FoundingFather</code> value
     * @param grey if the image should be in greyscale
     * @return an <code>Image</code> value
     */
    public static Image getFoundingFatherImage(FoundingFather father, boolean grey) {
        String resource = father.getId() + ".image";
        return grey
            ? ResourceManager.getGrayscaleImage(resource, 1f)
            : ResourceManager.getImage(resource);
    }

    public static Image getBellsImage(float scale) {
        return ResourceManager.getImage("model.goods.bells.image", scale);
    }

    /**
     * Returns the goods-image at the given index.
     *
     * @param goodsType The type of the goods-image to return.
     * @return The goods-image at the given index.
     */
    public Image getGoodsImage(GoodsType goodsType) {
        return getGoodsImage(goodsType, scalingFactor);
    }

    public static Image getGoodsImage(GoodsType goodsType, float scale) {
        return ResourceManager.getImage(goodsType.getId() + ".image", scale);
    }

    public static Image getGoodsImage(GoodsType goodsType, Dimension size) {
        return ResourceManager.getImage(goodsType.getId() + ".image", size);
    }

    public Image getBuildingImage(Building building) {
        return getBuildingImage(building.getType(), building.getOwner(), scalingFactor);
    }

    public static Image getBuildingImage(Building building, float scale) {
        return getBuildingImage(building.getType(), building.getOwner(), scale);
    }

    public static Image getBuildingImage(BuildingType buildingType, Player player, float scale) {
        String key = buildingType.getId() + "." + player.getNationNameKey() + ".image";
        if (!ResourceManager.hasResource(key)) {
            key = buildingType.getId() + ".image";
        }
        return ResourceManager.getImage(key, scale);
    }

    /**
     * Gets the owner chip for the settlement.
     *
     * @param is The <code>IndianSettlement</code> to check.
     * @return A chip.
     */
    public Image getIndianSettlementChip(IndianSettlement is) {
        String text = ResourceManager.getString("indianSettlementChip."
            + ((is.getType().isCapital()) ? "capital" : "normal"));
        Color background = is.getOwner().getNationColor();
        String key = "dynamic.indianSettlement." + text
            + "." + Integer.toHexString(background.getRGB())
            + "." + Float.toHexString(scalingFactor);
        Image img = ResourceManager.getImage(key);
        if (img == null) {
            img = createChip(text, Color.BLACK, background,
                             getForegroundColor(background));
            ResourceManager.addGameMapping(key, new ImageResource(img));
        }
        return img;
    }

    public Image getImage(FreeColGameObjectType type) {
        return ResourceManager.getImage(type.getId() + ".image", scalingFactor);
    }

    public static Image getImage(FreeColGameObjectType type, float scale) {
        return ResourceManager.getImage(type.getId() + ".image", scale);
    }

    /**
     * Returns the appropriate Image for Object.
     *
     * @param display The Object to display.
     * @return The appropriate Image.
     */
    public Image getObjectImage(Object display) {
        try {
            Image image;
            if (display instanceof Goods)
                display = ((Goods)display).getType();

            if (display instanceof GoodsType) {
                GoodsType goodsType = (GoodsType)display;
                image = getGoodsImage(goodsType);
            } else if (display instanceof Unit) {
                Unit unit = (Unit)display;
                image = getUnitImage(unit);
            } else if (display instanceof UnitType) {
                UnitType unitType = (UnitType)display;
                image = getUnitImage(unitType);
            } else if (display instanceof Settlement) {
                Settlement settlement = (Settlement)display;
                image = getSettlementImage(settlement);
            } else if (display instanceof LostCityRumour) {
                image = getMiscImage(ImageLibrary.LOST_CITY_RUMOUR);
            } else if (display instanceof Player) {
                image = getCoatOfArmsImage(((Player)display).getNation());
            } else {
                logger.warning("could not find image of unknown type for " + display);
                return null;
            }

            if (image == null) {
                logger.warning("could not find image for " + display);
                return null;
            }
            return image;
        } catch (Exception e) {
            logger.log(Level.WARNING, "could not find image", e);
            return null;
        }
    }

    /**
     * Returns the appropriate small Image for Object.
     *
     * @param display The Object to display.
     * @return The appropriate Image.
     */
    public Image getSmallObjectImage(Object display) {
        try {
            Image image = null;
            if (display instanceof Goods)
                display = ((Goods)display).getType();

            final float scale = 2f/3f;
            final float combinedScale = scalingFactor * scale;
            if (display instanceof GoodsType) {
                GoodsType goodsType = (GoodsType)display;
                image = getGoodsImage(goodsType, combinedScale);
            } else if (display instanceof Unit) {
                Unit unit = (Unit)display;
                image = getUnitImage(unit, combinedScale);
            } else if (display instanceof UnitType) {
                UnitType unitType = (UnitType)display;
                image = getUnitImage(unitType, combinedScale);
            } else if (display instanceof Settlement) {
                Settlement settlement = (Settlement)display;
                image = getSettlementImage(settlement, combinedScale);
            } else if (display instanceof LostCityRumour) {
                image = getMiscImage(ImageLibrary.LOST_CITY_RUMOUR,
                    combinedScale);
            } else if (display instanceof Player) {
                image = getCoatOfArmsImage(((Player)display).getNation(),
                    combinedScale);
            } else {
                logger.warning("could not find image of unknown type for "
                    + display);
                return null;
            }

            if (image == null) {
                logger.warning("could not find image for " + display);
                return null;
            }
            return image;
        } catch (Exception e) {
            logger.log(Level.WARNING, "could not find image", e);
            return null;
        }
    }

    /**
     * Returns the image with the given identifier.
     *
     * @param id The object identifier.
     * @return The image.
     */
    public Image getMiscImage(String id) {
        return getMiscImage(id, scalingFactor);
    }

    public static Image getMiscImage(String id, float scale) {
        return ResourceManager.getImage(id, scale);
    }

    /**
     * Gets the mission chip for a native settlement.
     *
     * @param owner The player that owns the mission.
     * @param expert True if the unit is an expert.
     * @return A suitable chip, or null if no mission is present.
     */
    public Image getMissionChip(Player owner, boolean expert) {
        Color background = owner.getNationColor();
        String key = "dynamic.mission." + ((expert) ? "expert" : "normal")
            + "." + Integer.toHexString(background.getRGB())
            + "." + Float.toHexString(scalingFactor);
        Image img = ResourceManager.getImage(key);
        if (img == null) {
            Color foreground = ResourceManager.getColor("mission."
                + ((expert) ? "expert" : "normal") + ".foreground.color");
            if (foreground == null) {
                foreground = (expert) ? Color.BLACK : Color.GRAY;
            }
            img = createChip(ResourceManager.getString("cross"),
                             Color.BLACK, background, foreground);
            ResourceManager.addGameMapping(key, new ImageResource(img));
        }
        return img;
    }

    /**
     * Returns the monarch-image for the given tile.
     *
     * @param nation The nation this monarch rules.
     * @return the monarch-image for the given nation.
     */
    public static Image getMonarchImage(Nation nation) {
        return ResourceManager.getImage(nation.getId() + ".monarch.image");
    }

    /**
     * Gets a chip for an occupation indicator, i.e. a small image with a
     * single letter or symbol that indicates the Unit's state.
     *
     * @param unit The <code>Unit</code> with the occupation.
     * @param text The text for the chip.
     * @return A suitable chip.
     */
    public Image getOccupationIndicatorChip(Unit unit, String text) {
        Color backgroundColor = unit.getOwner().getNationColor();
        Color foregroundColor = (unit.getState() == Unit.UnitState.FORTIFIED)
            ? Color.GRAY : getForegroundColor(backgroundColor);
        String key = "dynamic.occupationIndicator." + text
            + "." + Integer.toHexString(backgroundColor.getRGB())
            + "." + Float.toHexString(scalingFactor);
        Image img = ResourceManager.getImage(key);
        if (img == null) {
            img = createChip(text, Color.BLACK,
                             backgroundColor, foregroundColor);
            ResourceManager.addGameMapping(key, new ImageResource(img));
        }
        return img;
    }

    /**
     * Returns the overlay image for the given tile.
     *
     * @param tile The tile for which to return an image.
     * @return A pseudo-random terrain image.
     */
    public Image getOverlayImage(Tile tile) {
        return getOverlayImage(tile.getType(), tile.getId(), scalingFactor);
    }

    /**
     * Returns the overlay-image for the given type and scale.
     *
     * @param type The type of the terrain-image to return.
     * @param id A string used to get a random image.
     * @param scale The scale of the image to return.
     * @return The terrain-image at the given index.
     */
    public Image getOverlayImage(TileType type, String id, float scale) {
        return getRandomizedImage(type.getId() + ".overlay", id, scale);
    }

    private Image getRandomizedImage(String prefix, String id, float scale) {
        // TODO: Fix this brittle way of randomly choosing. Would be better to
        // keep the list of image names, sort and choose one available string.
        int count = getImageCount(prefix);
        if (count > 0) {
            String key = prefix + Math.abs(id.hashCode() % count) + ".image";
            if (ResourceManager.hasResource(key)) {
                return ResourceManager.getImage(key, scale);
            }
        }
        return null;
    }

    private int getImageCount(String prefix) {
        // TODO: Remove method after getRandomizedImage got changed. If its not
        // removed fix inefficient usage of getKeys discarding a result list
        // and then try to remove imageCounts cache, which may get outdated
        // when the ResourceMapping changes.
        if (!imageCounts.containsKey(prefix)) {
            imageCounts.put(prefix, ResourceManager.getKeys(prefix).size());
        }
        return imageCounts.get(prefix);
    }

    /**
     * Gets an image to represent the path of given path type.
     *
     * @param pt The <code>PathType</code>
     * @return The <code>Image</code>.
     */
    public static Image getPathImage(PathType pt) {
        return (pt == null) ? null
            : ResourceManager.getImage(pt.getImageKey());
    }

    /**
     * Gets an image to represent the path of the given <code>Unit</code>.
     *
     * @param u The <code>Unit</code>
     * @return The <code>Image</code>.
     */
    public static Image getPathImage(Unit u) {
        return (u == null) ? null
            : getPathImage(PathType.getPathType(u));
    }

    /**
     * Gets an image to represent the path of the given <code>Unit</code>.
     *
     * @param pt The <code>PathType</code>
     * @return The <code>Image</code>.
     */
    public static Image getPathNextTurnImage(PathType pt) {
        return (pt == null) ? null
            : ResourceManager.getImage(pt.getNextTurnImageKey());
    }

    /**
     * Gets an image to represent the path of the given <code>Unit</code>.
     *
     * @param u The <code>Unit</code>
     * @return The <code>Image</code>.
     */
    public static Image getPathNextTurnImage(Unit u) {
        return (u == null) ? null
            : getPathNextTurnImage(PathType.getPathType(u));
    }

    /**
     * Returns the river image with the given style.
     *
     * @param style a <code>TileImprovementStyle</code> value
     * @return The image with the given style.
     */
    public Image getRiverImage(TileImprovementStyle style) {
        return getRiverImage(style, scalingFactor);
    }

    /**
     * Returns the river image with the given style.
     *
     * @param style a <code>TileImprovementStyle</code> value
     * @param scale a <code>double</code> value
     * @return The image with the given style.
     */
    public static Image getRiverImage(TileImprovementStyle style, float scale) {
        return getRiverImage(style.getString(), scale);
    }

    /**
     * Returns the river image with the given style.
     *
     * @param style a <code>String</code> value
     * @param scale a <code>double</code> value
     * @return The image with the given style.
     */
    public static Image getRiverImage(String style, float scale) {
        return ResourceManager.getImage("model.tile.river" + style, scale);
    }

    /**
     * Returns the river mouth terrain-image for the direction and magnitude.
     *
     * @param direction a <code>Direction</code> value
     * @param magnitude an <code>int</code> value
     * @param x The x-coordinate of the location of the tile that is being
     *            drawn (ignored).
     * @param y The x-coordinate of the location of the tile that is being
     *            drawn (ignored).
     * @return The terrain-image at the given index.
     */
    public Image getRiverMouthImage(Direction direction, int magnitude,
                                    int x, int y) {
        String key = "model.tile.delta_" + direction
            + (magnitude == 1 ? "_small" : "_large");
        return ResourceManager.getImage(key, scalingFactor);
    }

    /**
     * Returns the graphics that will represent the given settlement.
     *
     * @param settlement The settlement whose graphics type is needed.
     * @return The graphics that will represent the given settlement.
     */
    public Image getSettlementImage(Settlement settlement) {
        return getSettlementImage(settlement, scalingFactor);
    }

    /**
     * Returns the graphics that will represent the given settlement.
     *
     * @param settlement The settlement whose graphics type is needed.
     * @param scale a <code>double</code> value
     * @return The graphics that will represent the given settlement.
     */
    public static Image getSettlementImage(Settlement settlement, float scale) {
        return ResourceManager.getImage(settlement.getImageKey(), scale);
    }

    public static Image getSettlementImage(Settlement settlement, Dimension size) {
        return ResourceManager.getImage(settlement.getImageKey(), size);
    }

    /**
     * Returns the graphics that will represent the given settlement.
     *
     * @param settlementType The type of settlement whose graphics
     *     type is needed.
     * @return The graphics that will represent the given settlement.
     */
    public Image getSettlementImage(SettlementType settlementType) {
        return getSettlementImage(settlementType, scalingFactor);
    }

    public static Image getSettlementImage(SettlementType settlementType,
                                    float scale) {
        return ResourceManager.getImage(settlementType.getId() + ".image",
                                        scale);
    }

    /**
     * Gets an image with a string of a given color and with
     * a black border around the glyphs.
     *
     * @param g A <code>Graphics</code>-object for getting the font metrics.
     * @param text The <code>String</code> to make an image of.
     * @param color The <code>Color</code> to use for the text.
     * @param font The <code>Font</code> to display the text with.
     * @return The image that was created.
     */
    public static Image getStringImage(Graphics g, String text, Color color,
                                Font font) {
        if (color == null) {
            logger.warning("createStringImage called with color null");
            color = Color.WHITE;
        }

        // Lookup in the cache if the image has been generated already
        String key = "dynamic.stringImage." + text
            + "." + font.getFontName().replace(' ', '-')
            + "." + Integer.toString(font.getSize())
            + "." + Integer.toHexString(color.getRGB());
        Image img = ResourceManager.getImage(key);
        if (img == null) {
            // create an image of the appropriate size
            FontMetrics fm = g.getFontMetrics(font);
            BufferedImage bi = new BufferedImage(fm.stringWidth(text) + 4,
                fm.getMaxAscent() + fm.getMaxDescent(),
                BufferedImage.TYPE_INT_ARGB);
            // draw the string with selected color
            Graphics2D big = bi.createGraphics();
            big.setColor(getStringBorderColor(color));
            big.setFont(font);
            big.drawString(text, 2, fm.getMaxAscent());

            // draw the border around letters
            int borderWidth = 1;
            int borderColor = getStringBorderColor(color).getRGB();
            int srcRGB, dstRGB, srcA;
            for (int biY = 0; biY < bi.getHeight(); biY++) {
                for (int biX = borderWidth; biX < bi.getWidth() - borderWidth; biX++) {
                    int biXI = bi.getWidth() - biX - 1;
                    for (int d = 1; d <= borderWidth; d++) {
                        // left to right
                        srcRGB = bi.getRGB(biX, biY);
                        srcA = (srcRGB >> 24) & 0xFF;
                        dstRGB = bi.getRGB(biX - d, biY);
                        if (dstRGB != borderColor) {
                            if (srcA > 0) {
                                bi.setRGB(biX, biY, borderColor);
                                bi.setRGB(biX - d, biY, srcRGB);
                            }
                        }
                        // right to left
                        srcRGB = bi.getRGB(biXI, biY);
                        srcA = (srcRGB >> 24) & 0xFF;
                        dstRGB = bi.getRGB(biXI + d, biY);
                        if (dstRGB != borderColor) {
                            if (srcA > 0) {
                                bi.setRGB(biXI, biY, borderColor);
                                bi.setRGB(biXI + d, biY, srcRGB);
                            }
                        }
                    }
                }
            }
            for (int biX = 0; biX < bi.getWidth(); biX++) {
                for (int biY = borderWidth; biY < bi.getHeight() - borderWidth; biY++) {
                    int biYI = bi.getHeight() - biY - 1;
                    for (int d = 1; d <= borderWidth; d++) {
                        // top to bottom
                        srcRGB = bi.getRGB(biX, biY);
                        srcA = (srcRGB >> 24) & 0xFF;
                        dstRGB = bi.getRGB(biX, biY - d);
                        if (dstRGB != borderColor) {
                            if (srcA > 0) {
                                bi.setRGB(biX, biY, borderColor);
                                bi.setRGB(biX, biY - d, srcRGB);
                            }
                        }
                        // bottom to top
                        srcRGB = bi.getRGB(biX, biYI);
                        srcA = (srcRGB >> 24) & 0xFF;
                        dstRGB = bi.getRGB(biX, biYI + d);
                        if (dstRGB != borderColor) {
                            if (srcA > 0) {
                                bi.setRGB(biX, biYI, borderColor);
                                bi.setRGB(biX, biYI + d, srcRGB);
                            }
                        }
                    }
                }
            }

            big.setColor(color);
            big.drawString(text, 2, fm.getMaxAscent());

            ResourceManager.addGameMapping(key, new ImageResource(bi));
            img = ResourceManager.getImage(key);
        }
        return img;
    }

    /**
     * Returns the terrain-image for the given type.
     *
     * @param type The type of the terrain-image to return.
     * @param x The x-coordinate of the location of the tile that is being
     *            drawn.
     * @param y The x-coordinate of the location of the tile that is being
     *            drawn.
     * @return The terrain-image at the given index.
     */
    public Image getTerrainImage(TileType type, int x, int y) {
        return getTerrainImage(type, x, y, scalingFactor);
    }

    public static Image getTerrainImage(TileType type, int x, int y, float scale) {
        String key = (type == null) ? "model.tile.unexplored" : type.getId();
        return ResourceManager.getImage(key + ".center"
            + (isEven(x, y) ? "0" : "1") + ".image", scale);
    }

    public Image getSmallUnitImage(Unit unit) {
        return getUnitImage(unit.getType(), unit.getRole().getId(),
            unit.hasNativeEthnicity(), false, scalingFactor * (2f/3f));
    }

    public Image getSmallUnitImage(Unit unit, boolean grayscale) {
        return getUnitImage(unit.getType(), unit.getRole().getId(),
            unit.hasNativeEthnicity(), grayscale, scalingFactor * (2f/3f));
    }

    public Image getUnitImage(Unit unit) {
        return getUnitImage(unit.getType(), unit.getRole().getId(),
            unit.hasNativeEthnicity(), false, scalingFactor);
    }

    public Image getUnitImage(Unit unit, boolean grayscale) {
        return getUnitImage(unit.getType(), unit.getRole().getId(),
            unit.hasNativeEthnicity(), grayscale, scalingFactor);
    }

    public static Image getUnitImage(Unit unit, float scale) {
        return getUnitImage(unit.getType(), unit.getRole().getId(),
            unit.hasNativeEthnicity(), false, scale);
    }

    public Image getUnitImage(UnitType unitType) {
        return getUnitImage(unitType, unitType.getDisplayRoleId(),
            false, false, scalingFactor);
    }

    public static Image getUnitImage(UnitType unitType, float scale) {
        return getUnitImage(unitType, unitType.getDisplayRoleId(),
            false, false, scale);
    }

    public static Image getUnitImage(UnitType unitType, String roleId,
                                     float scale) {
        return getUnitImage(unitType, roleId, false, false, scale);
    }

    public static Image getUnitImage(UnitType unitType, String roleId,
                                     boolean grayscale, float scale) {
        return getUnitImage(unitType, roleId, false, grayscale, scale);
    }

    /**
     * Gets the image that will represent a given unit.
     *
     * @param unitType The type of unit to be represented.
     * @param roleId The id of the unit role.
     * @param nativeEthnicity If true the unit is a former native.
     * @param grayscale If true draw in inactive/disabled-looking state.
     * @param scale How much the image is scaled.
     * @return A suitable <code>Image</code>.
     */
    public static Image getUnitImage(UnitType unitType, String roleId,
                                      boolean nativeEthnicity,
                                      boolean grayscale, float scale) {
        // units that can only be native don't need the .native key part
        if (unitType.hasAbility(Ability.BORN_IN_INDIAN_SETTLEMENT)) {
            nativeEthnicity = false;
        }

        // try to get an image matching the key
        String roleQual = (Role.isDefaultRoleId(roleId)) ? ""
            : "." + Role.getRoleSuffix(roleId);
        String key = unitType.getId() + roleQual
            + ((nativeEthnicity) ? ".native" : "")
            + ".image";
        if (!ResourceManager.hasResource(key) && nativeEthnicity) {
            key = unitType.getId() + roleQual + ".image";
        }
        Image image = (grayscale)
            ? ResourceManager.getGrayscaleImage(key, scale)
            : ResourceManager.getImage(key, scale);
        if (image == null) {
            String complain = "No image icon for: unitType=" + unitType.getId()
                + " role=" + roleId + " native=" + nativeEthnicity
                + " gray=" + grayscale + " scale=" + scale
                + " roleQual=" + roleQual + " key=" + key;
            logger.warning(complain);
            return null;
            // Consider throwing a RuntimeException.
        }
        return image;
    }
}
