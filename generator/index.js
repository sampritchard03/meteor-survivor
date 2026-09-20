const data = require("minecraft-data")("26.1")

const blockDrops = require("./blockDrops");
const { Recipe } = require("prismarine-recipe")("26.1")

const newData = {}

for (let name of Object.keys(data.itemsByName)) {
    if (Object.keys(data.blocksByName).includes(name)) {
        let drops = data.blocksByName[name].drops.map(num => data.itemsArray[num].name);

        newData[name] = {drops, dropsFrom:[], recipes:[], overrideName:""}
    } else {
        newData[name] = {drops:[], dropsFrom:[], recipes:[], overrideName:""}
    }
}

// redstone dust
for (let name of Object.keys(data.itemsByName)) {

    // redstone ore
    for (let otherName of Object.keys(data.itemsByName)) {

        // if redstone ore drops redstone dust
        if (newData[name].drops.includes(otherName)) {

            //add redstone ore to redstone dust dropsFrom
            newData[otherName].dropsFrom.push(name)

        }
    }
}

for (let type in data.items) {
    const recipes = Recipe.find(type)

    for (let recipe of recipes) {
        const ingredients = []
        for (let item of recipe.delta) {
            if (item.count < 0)
                ingredients.push({
                    validNames: [data.items[item.id].name],
                    count: -item.count
                })
        }
        newData[data.items[type].name].recipes.push({resultCount:recipe.result.count, ingredients, requiresTable:recipe.requiresTable})
    }

}

blockDrops(newData)
//x = 2
//y() = 2