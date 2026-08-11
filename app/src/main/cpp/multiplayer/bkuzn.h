//
// Created by Bkuzn on 20.10.2025.
//
#pragma once

#include <string>
// by Bkuzn (Egor Kuzn t.me/bkuzn)
struct Bkuzn
{
    std::string szHost = "84.54.82.234";
    int iPort = 7777;
};

inline Bkuzn GetBkuzn()
{
    Bkuzn config;
    return config;
}