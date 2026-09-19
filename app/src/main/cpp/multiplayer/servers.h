#pragma once

#include <string>
struct Servers
{
    std::string szHost = "185.207.214.14";
    int iPort = 3277;
};

inline Servers GetServers()
{
    Servers config;
    return config;
}