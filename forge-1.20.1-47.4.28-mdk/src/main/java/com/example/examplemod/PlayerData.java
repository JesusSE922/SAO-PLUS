package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;

public class PlayerData
{
    public static final int MAX_LEVEL = 100;
    public static final int ATTRIBUTE_POINTS_PER_LEVEL = 3;

    private int level = 1;
    private int experience;
    private int strength = 5;
    private int agility = 5;
    private int vitality = 5;
    private int intelligence = 5;
    private int availablePoints;

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = Math.max(1, Math.min(MAX_LEVEL, level));
    }

    public int getExperience()
    {
        return experience;
    }

    public void setExperience(int experience)
    {
        this.experience = Math.max(0, experience);
    }

    public int getStrength()
    {
        return strength;
    }

    public void setStrength(int strength)
    {
        this.strength = Math.max(0, strength);
    }

    public int getAgility()
    {
        return agility;
    }

    public void setAgility(int agility)
    {
        this.agility = Math.max(0, agility);
    }

    public int getVitality()
    {
        return vitality;
    }

    public void setVitality(int vitality)
    {
        this.vitality = Math.max(0, vitality);
    }

    public int getIntelligence()
    {
        return intelligence;
    }

    public void setIntelligence(int intelligence)
    {
        this.intelligence = Math.max(0, intelligence);
    }

    public int getAvailablePoints()
    {
        return availablePoints;
    }

    public void setAvailablePoints(int availablePoints)
    {
        this.availablePoints = Math.max(0, availablePoints);
    }

    public void copyFrom(PlayerData other)
    {
        level = other.level;
        experience = other.experience;
        strength = other.strength;
        agility = other.agility;
        vitality = other.vitality;
        intelligence = other.intelligence;
        availablePoints = other.availablePoints;
    }

    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Level", level);
        tag.putInt("Experience", experience);
        tag.putInt("Strength", strength);
        tag.putInt("Agility", agility);
        tag.putInt("Vitality", vitality);
        tag.putInt("Intelligence", intelligence);
        tag.putInt("AvailablePoints", availablePoints);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag)
    {
        setLevel(tag.contains("Level") ? tag.getInt("Level") : 1);
        setExperience(tag.getInt("Experience"));
        setStrength(tag.contains("Strength") ? tag.getInt("Strength") : 5);
        setAgility(tag.contains("Agility") ? tag.getInt("Agility") : 5);
        setVitality(tag.contains("Vitality") ? tag.getInt("Vitality") : 5);
        setIntelligence(tag.contains("Intelligence") ? tag.getInt("Intelligence") : 5);
        setAvailablePoints(tag.getInt("AvailablePoints"));
    }
}
