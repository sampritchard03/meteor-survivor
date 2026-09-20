const fs = require("fs");

module.exports = (newData) => {
    function setName(name, newName) {
        if (!newData[name]) console.log(name)
        else newData[name].overrideName = newName
    }

    function getName(name) {
        return newData[name].overrideName == "" ? name.toUpperCase() : newData[name].overrideName
        
    }

    function setCopperNames(name, waxed) {
        if (waxed) {
            setName("waxed_"+name, name.toUpperCase()+".waxed().unaffected()")
            setName("waxed_exposed_"+name, name.toUpperCase()+".waxed().exposed()")
            setName("waxed_weathered_"+name, name.toUpperCase()+".waxed().weathered()")
            setName("waxed_oxidized_"+name, name.toUpperCase()+".waxed().oxidized()")
        } else {
            setName(name, name.toUpperCase()+".weathering().unaffected()")
            setName("exposed_"+name, name.toUpperCase()+".weathering().exposed()")
            setName("weathered_"+name, name.toUpperCase()+".weathering().weathered()")
            setName("oxidized_"+name, name.toUpperCase()+".weathering().oxidized()")
        }
        
    }

    function setColoredNames(name, dyed) {
        const extensions = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"]
        const functions = ["white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "lightGray", "cyan", "purple", "blue", "brown", "green", "red", "black"]

        for (let i = 0; i < extensions.length; i++) {
            setName(extensions[i]+"_"+name, (dyed?"DYED_":"")+name.toUpperCase()+"."+functions[i]+"()")
        }
    }

    setName("cut_sandstone_slab", "CUT_STANDSTONE_SLAB") // lol

    setName("copper_block", "COPPER_BLOCK.weathering().unaffected()")
    setName("exposed_copper", "COPPER_BLOCK.weathering().unaffected()")
    setName("weathered_copper", "COPPER_BLOCK.weathering().unaffected()")
    setName("oxidized_copper", "COPPER_BLOCK.weathering().unaffected()")

    setCopperNames("chiseled_copper", false)
    setCopperNames("cut_copper", false)
    setCopperNames("cut_copper_stairs", false)
    setCopperNames("cut_copper_slab", false)

    setName("waxed_copper_block", "COPPER_BLOCK.waxed().unaffected()")
    setName("waxed_exposed_copper", "COPPER_BLOCK.waxed().unaffected()")
    setName("waxed_weathered_copper", "COPPER_BLOCK.waxed().unaffected()")
    setName("waxed_oxidized_copper", "COPPER_BLOCK.waxed().unaffected()")

    setCopperNames("chiseled_copper", true)
    setCopperNames("cut_copper", true)
    setCopperNames("cut_copper_stairs", true)
    setCopperNames("cut_copper_slab", true)

    setColoredNames("wool", false)

    setCopperNames("copper_bars", false)
    setCopperNames("copper_bars", true)
    setCopperNames("copper_chain", false)
    setCopperNames("copper_chain", true)

    setColoredNames("terracotta", true)
    setColoredNames("carpet", false)
    setColoredNames("stained_glass", false)
    setColoredNames("stained_glass_pane", false)
    setColoredNames("shulker_box", true)
    setColoredNames("glazed_terracotta", false)
    setColoredNames("concrete", false)
    setColoredNames("concrete_powder", false)

    setCopperNames("lightning_rod", false)
    setCopperNames("lightning_rod", true)
    setCopperNames("copper_door", false)
    setCopperNames("copper_door", true)
    setCopperNames("copper_trapdoor", false)
    setCopperNames("copper_trapdoor", true)

    setColoredNames("harness", false)
    setColoredNames("bundle", true)
    setColoredNames("dye", false)
    setColoredNames("bed", false)
    setColoredNames("banner", false)

    setCopperNames("copper_lantern", false)
    setCopperNames("copper_lantern", true)

    setColoredNames("candle", true)

    setCopperNames("copper_grate", false)
    setCopperNames("copper_grate", true)
    setCopperNames("copper_bulb", false)
    setCopperNames("copper_bulb", true)
    setCopperNames("copper_chest", false)
    setCopperNames("copper_chest", true)
    setCopperNames("copper_golem_statue", false)
    setCopperNames("copper_golem_statue", true)

    var output = `package com.example.addon.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.addon.item.ItemData.Data;
import com.example.addon.utils.Pair;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemData {
    public static final Map<Item, Data> allData = new HashMap<>();
    
`

    for (let name of Object.keys(newData)) {
        output += "    public static Data "+name.toUpperCase()+";\n"
    }

    output += `
    

    public static void initialize0() {

`
    var i = 0;
    var k = 0;

    for (let name of Object.keys(newData)) {
        const finalName = getName(name)
        if (finalName == "AIR" || finalName.includes("_DRY_GRASS")) continue

        output += "        "+name.toUpperCase()+" = register(Items."+finalName+", List.of("

        let hasLength = false;
        for (let dropName of newData[name].drops) {
            const finalDropName = getName(dropName)
            output += "Items."+finalDropName+", "
            hasLength = true;
        }

        if (hasLength) output = output.slice(0, output.length-2)

        output += "), List.of("

        hasLength = false;
        for (let dropsFromName of newData[name].dropsFrom) {
            const finalDropFromName = getName(dropsFromName)
            output += "Items."+finalDropFromName+", "
            hasLength = true;
        }

        if (hasLength) output = output.slice(0, output.length-2)

        output += "), List.of("

        hasLength = false;
        for (let recipe of newData[name].recipes) {
            output += "new Recipe(Items."+finalName+", "+recipe.resultCount+", List.of("

            var hasLength1 = false;
            for (let ingredient of recipe.ingredients) {
                output += "new Pair(List.of(Items."+getName(ingredient.name)+"), "+ingredient.count+"), "
                hasLength1 = true
            }
            if (hasLength1) output = output.slice(0, output.length-2)
            output += "), "+recipe.requiresTable+"), "
            hasLength = true;
        }

        if (hasLength) output = output.slice(0, output.length-2)

        output += "));\n"

        if (i % Math.floor(Object.keys(newData).length/4) == 0) {
            if (k > 0)
                output += `
        
    }
            
    
    public static void initialize`+k+`() {
    
`
            k++;
        }

        i++;
    }

    output += `

    }

    private static Data register(Item item, List<Item> drops, List<Item> dropsFrom, List<Recipe> _recipes) {
        Data drop = new Data(drops, dropsFrom, _recipes);
        allData.put(item, drop);
        return drop;
    }

    public static class Data {
        public final List<Item> drops;
        public final List<Item> dropsFrom;
        public final List<Recipe> recipes;

        public Data(List<Item> _drops, List<Item> _dropsFrom, List<Recipe> _recipes) {
            drops = _drops; dropsFrom = _dropsFrom; recipes = _recipes;
        }
    }
}`

    fs.writeFileSync("./ItemData.java", output)
}