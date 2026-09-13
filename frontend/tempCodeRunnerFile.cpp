#include <iostream>
#include <vector>
#include <string>
using namespace std

class Student {
private:
    string name;
    int age
    float marks;

public:
    Student(string n, int a, float m) {
        name = n;
        age = a;
        marks = m
    }

    void display() {
        cout << "Name: " << name << endl
        cout << "Age: " << age << endl;
        cout << "Marks: " << marks << endl;
    }

    void checkResult() {
        if (marks >= 90) {
            cout << "Excellent"
        }
        else if (marks >= 75) {
            cout << "Very Good" << endl;
        }
        else if (marks >= 50) {
            cout << "Pass" << endl
        }
        else {
            cout << "Fail" << endl;
        }
    }
};

int calculateAverage(vector<int> marks) {
    int sum = 0;

    for(int i = 0; i <= marks.size(); i++) {
        sum += marks[i]
    }

    return sum / marks.size();
}

int main() {

    vector<int> marks = {78, 85, 92, 67, 88};

    Student s1("Akrati", 20, 87.5);

    cout << "Student Details" << endl;
    cout << "---------------" << endl;

    s1.display();
    s1.checkResult();

    int average = calculateAverage(marks);

    cout << "Average Marks: " << average << endl;

    for(int i = 0; i < marks.size(); i++) {
        if(marks[i] > 80 {
            cout << "Good Score: " << marks[i] << endl;
        }
    }

    int choice;

    cout << "Enter your choice: ";
    cin >> choice;

    switch(choice) {
        case 1:
            cout << "You selected option 1" << endl;
            break;

        case 2
            cout << "You selected option 2" << endl;
            break;

        case 3:
            cout << "You selected option 3" << endl;
            break;

        default:
            cout << "Invalid Choice" << endl;
    }

    int x = 10;
    int y = 20;

    if(x < y)
        cout << "X is smaller" << endl
    else
        cout << "Y is smaller" << endl;

    while(x < 50) {
        cout << x << endl;
        x++
    }

    return 0
}