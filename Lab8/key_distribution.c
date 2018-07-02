#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>
#include <math.h>
 
int main(int argc, char **argv)
{
    int KEYRING_SIZE = 100;             //key ring size
    int KEYPOOL_SIZE = 10000;           //key pool size
    int n = 1024;                   //the size of the network is equal to 2^10=1024
    int coordinates[700][700] = {{0}};          //dimension of the grid 700X700
    double r = 0;                   //transmission range
    double distance = 0, avgDistance = 0;       //euclidean distance and average distance
    char GP_INPUT_DATA_FILE[10] = "plot.dat";   //name of the file used to store sensor into the plot
    char GP_INPUT_PLOT_FILE[10] = "plot.gp";    //name of the file used to generate the plot
 
 
    //sensor node representation
    typedef struct {
        int x;          // x-coordinate of sensor
        int y;          // y-coordinate of sensor
        int *keyring;       // key ring
        int directkeynbsize;    // number of direct-key neighbors of a sensor node
        int sharedkeynbsize;    // number of shared-key neighbors of a sensor node
        int *directnb;      // list of direct-key neighbors of a sensor node
        int *sharednb;      // list of shared-key neighbors of a sensor node
    } sensor;
 
    //***********************************//
    //**********Pre-Deployment***********//
    //***********************************//
    sensor sensorList[n];
 
    FILE *fpgnuplotData = fopen(GP_INPUT_DATA_FILE, "w");
    FILE *fplot = fopen(GP_INPUT_PLOT_FILE, "w");
 
    int count = n, i, x, y, j, k, l, m;
    //  Generate the non-repeating random pairs of coordinates x's and y's in order to fill the grid with sensors
    while(count--)
        (coordinates[x = rand()%700][y = rand()%700] == 0)? coordinates[x][y] = 1 : count++;
 
    //  Print the non-repeating random pairs of coordinates into the file fpgnuplotData
    count = 0;
    for(i = 0; i < 700; i++)
    {
        for(j = 0; j < 700; j++)
        {
            if(coordinates[i][j])
            {
                fprintf(fpgnuplotData, "%d %d", i,j);
                sensorList[count].x = i;
                sensorList[count].y = j;
                if(++count != n)
                    fprintf(fpgnuplotData, "\n");
            }
        }
    }
   
    printf("\nReading data file...\n");
    fprintf(fplot, "plot '%s'", GP_INPUT_DATA_FILE);
 
    //  Close the handles of each file
    fclose(fpgnuplotData);
    fclose(fplot);
   
    //  Opening the distribution of the Wireless Sensor Network
    system("gnuplot -p 'plot.gp'");
 
    //  Using Euclidean Distance calculate the total distance and then calculate the average distance of the network
    for(i = 0; i < n; i++)
        for(j = 0; j < n; j++)
            if(i != j)
            count++, distance += sqrt(pow((sensorList[i].x - sensorList[j].x), 2) + pow((sensorList[i].y - sensorList[j].y), 2));
 
    printf("Average distance = %.2lf m\n", (avgDistance = distance / count));
   
    //  Approximate the range (r) value. r = 30
    r = round (avgDistance / 12);
	
    //  Scaling transmission range r
    printf("Transmission range of sensor nodes = %.2lf m (Approximation considered)\n", r);
 
    printf("\nComputing direct-key neighbors...\n");
   
    //  File used to store the direct-key neighbors pairs
    FILE * fpsharedkeynb;
    fpsharedkeynb=fopen("directkeys.pairs", "w");
    //  Calculate the direct-key neighbors
    int singleSensorCounter = 0;
    count = 0;
    for(i = 0; i < n; i++)
    {
        singleSensorCounter = 0;
        sensorList[i].directnb = (int*)malloc(sizeof(int) * 1000);
        for(j = 0; j < n; j++)
        {
            if(i != j)
            {
                if(r >= sqrt(pow((sensorList[i].x - sensorList[j].x), 2) + pow((sensorList[i].y - sensorList[j].y), 2)))
                {
                    sensorList[i].directnb[singleSensorCounter++] = j;
                    //  Printing pairs of sensors that are direct-key neighbors
                    fprintf(fpsharedkeynb, "(%d, %d) --> (%d, %d)\n", sensorList[i].x, sensorList[i].y, sensorList[j].x, sensorList[j].y);
                    count++;
                }
            }
        }
        sensorList[i].directkeynbsize = singleSensorCounter;
    }
 
    fclose(fpsharedkeynb);
 
    //**********Distribuiting the keys to each sensor***********//
    printf("Distributing keys...\n");
 
    //  Set the array of Key_poolsize so that we can avoid the repeatation in random numbers
    int randomKeyPoolArray[10000] = {0}, key = 0;
 
    for(i = 0; i < n; i++)
    {
        sensorList[i].keyring = (int*)malloc(sizeof(int) * (KEYRING_SIZE + 200));
        for(j = 0; j < KEYRING_SIZE;)
        {
            if(randomKeyPoolArray[key = rand() % KEYPOOL_SIZE] == 0)
            {
                randomKeyPoolArray[key] = 1;
                sensorList[i].keyring[j] = key;
                j++;
            }
        }
 
        memset(randomKeyPoolArray, 0, sizeof randomKeyPoolArray);
    }
   
    //**********************************************//
    //************Shared-key neighbors*************//
    //**********************************************//
   
    printf("Discovering shared-key neighbors...\n");
    FILE * fpsharedkey;
    fpsharedkey=fopen("sharekeys.keys", "w");
 
    //  Calculate the key neighbors for the sensor nodes
    int directnblist = 0;
    for(i = 0; i < n; i++)
    {
        //  Get the list of shared-key neighbors of current sensor node
        int sharednbCount = 0;
        sensorList[i].sharednb = (int*)malloc(sizeof(int) * 200);
        for(j = 0; j < sensorList[i].directkeynbsize; j++)
        {
            //  Check each direct-key neighbor sensor node key list with the current
            //  sensor node, if they have common key then they are key neighbors
            int flag = 0;
            directnblist = sensorList[i].directnb[j];
 
            //  Get the keyring of the current sensor node
            for(k = 0; k < KEYRING_SIZE; k++)
            {
                //  Get the key ring of the current direct-key neighbor node
                for(l = 0; l < KEYRING_SIZE; l++)
                {
                    if(sensorList[i].keyring[k] == sensorList[directnblist].keyring[l])
                    {
                        fprintf(fpsharedkey, "%d share a key with %d and key is %d\n",i, directnblist, sensorList[directnblist].keyring[l]);
                        sensorList[i].sharednb[sharednbCount++] = directnblist;
                        flag = 1;
                        break;
                    }
                }
                if(flag == 1)
                    break;
            }
        }
        sensorList[i].sharedkeynbsize = sharednbCount;
    }
   
    fclose(fpsharedkey);
 
    return 0;
}