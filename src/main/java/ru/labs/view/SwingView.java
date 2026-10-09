package ru.labs.view;

import ru.labs.core.Comparator;
import ru.labs.core.CyclicList;
import ru.labs.core.SparseMatrix;
import ru.labs.core.UserFactory;
import ru.labs.core.UserType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class SwingView extends JFrame {
    private final UserFactory factory;
    private CyclicList list;
    private UserType currentType;

    private JLabel typeLabel;
    private JTextArea listDisplay;
    private JPanel mainPanel;

    public SwingView() {
        this.factory = new UserFactory();
        this.list = new CyclicList();
        this.currentType = factory.getBuilderByName("Integer");

        setTitle("Лабораторная работа №1 - Циклический список");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        initializeUI();
    }

    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верхняя панель: выбор типа
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Центральная панель: отображение списка и операции
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Нижняя панель: кнопки
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Тип данных"));

        panel.add(new JLabel("Выберите тип:"));

        ArrayList<String> typeNames = factory.getTypeNameList();
        JComboBox<String> typeCombo = new JComboBox<>(typeNames.toArray(new String[0]));
        typeCombo.setSelectedItem("Integer");
        typeCombo.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            currentType = factory.getBuilderByName(selectedType);
            list = new CyclicList();
            updateTypeLabel();
            updateListDisplay();
        });
        panel.add(typeCombo);

        typeLabel = new JLabel("Текущий тип: Integer");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(typeLabel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Список"));

        listDisplay = new JTextArea(10, 40);
        listDisplay.setEditable(false);
        listDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        listDisplay.setText("Список пуст");

        JScrollPane scrollPane = new JScrollPane(listDisplay);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel operationPanel = createOperationPanel();
        panel.add(operationPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createOperationPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Операции"));

        JButton addBtn = new JButton("Добавить");
        addBtn.addActionListener(e -> addElement());
        panel.add(addBtn);

        JButton getBtn = new JButton("Получить");
        getBtn.addActionListener(e -> getElement());
        panel.add(getBtn);

        JButton insertBtn = new JButton("Вставить");
        insertBtn.addActionListener(e -> insertElement());
        panel.add(insertBtn);

        JButton removeBtn = new JButton("Удалить");
        removeBtn.addActionListener(e -> removeElement());
        panel.add(removeBtn);

        JButton forEachBtn = new JButton("ForEach");
        forEachBtn.addActionListener(e -> forEachDemo());
        panel.add(forEachBtn);

        JButton firstThatBtn = new JButton("FirstThat");
        firstThatBtn.addActionListener(e -> firstThatDemo());
        panel.add(firstThatBtn);

        JButton sortBtn = new JButton("Сортировка");
        sortBtn.addActionListener(e -> sortList());
        panel.add(sortBtn);

        JButton clearBtn = new JButton("Очистить");
        clearBtn.addActionListener(e -> {
            list = new CyclicList();
            updateListDisplay();
        });
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Файловые операции"));

        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(e -> saveList());
        panel.add(saveBtn);

        JButton loadBtn = new JButton("Загрузить");
        loadBtn.addActionListener(e -> loadList());
        panel.add(loadBtn);

        JButton debugBtn = new JButton("Отладка");
        debugBtn.addActionListener(e -> showDebugMenu());
        panel.add(debugBtn);

        JButton exitBtn = new JButton("Выход");
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);

        return panel;
    }

    private void addElement() {
        String input = JOptionPane.showInputDialog(this,
                "Введите значение для типа " + currentType.typeName() + ":",
                "Добавить элемент",
                JOptionPane.PLAIN_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                Object value = currentType.parseValue(input);
                list.add(value);
                updateListDisplay();
                JOptionPane.showMessageDialog(this, "Элемент добавлен: " + value, "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void getElement() {
        String indexStr = JOptionPane.showInputDialog(this, "Введите индекс:", "0");
        if (indexStr != null) {
            try {
                int index = Integer.parseInt(indexStr);
                Object value = list.get(index);
                JOptionPane.showMessageDialog(this, "Элемент[" + index + "] = " + value, "Результат", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void insertElement() {
        String indexStr = JOptionPane.showInputDialog(this, "Введите индекс:", "0");
        if (indexStr != null) {
            try {
                int index = Integer.parseInt(indexStr);
                String input = JOptionPane.showInputDialog(this,
                        "Введите значение для типа " + currentType.typeName() + ":",
                        "Вставить элемент",
                        JOptionPane.PLAIN_MESSAGE);

                if (input != null && !input.trim().isEmpty()) {
                    Object value = currentType.parseValue(input);
                    list.insert(index, value);
                    updateListDisplay();
                    JOptionPane.showMessageDialog(this, "Элемент вставлен: " + value, "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeElement() {
        String indexStr = JOptionPane.showInputDialog(this, "Введите индекс:", "0");
        if (indexStr != null) {
            try {
                int index = Integer.parseInt(indexStr);
                Object removed = list.remove(index);
                updateListDisplay();
                JOptionPane.showMessageDialog(this, "Удалён элемент: " + removed, "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void forEachDemo() {
        StringBuilder sb = new StringBuilder();
        list.forEach(obj -> sb.append(obj).append("\n"));

        if (sb.length() == 0) {
            JOptionPane.showMessageDialog(this, "Список пуст", "Результат", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            JOptionPane.showMessageDialog(this, scrollPane, "Результат ForEach", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void firstThatDemo() {
        String pattern = JOptionPane.showInputDialog(this, "Введите шаблон поиска:", "");
        if (pattern != null) {
            try {
                Object found = list.firstThat(obj -> {
                    if (obj instanceof Integer) {
                        return obj.toString().contains(pattern);
                    }
                    if (obj instanceof SparseMatrix) {
                        SparseMatrix mm = (SparseMatrix) obj;
                        return (mm.getRows() + "x" + mm.getCols()).contains(pattern);
                    }
                    return false;
                });

                if (found == null) {
                    JOptionPane.showMessageDialog(this, "Ничего не найдено", "Результат", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Найдено: " + found, "Результат", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sortList() {
        try {
            Comparator comparator = currentType.getTypeComparator();
            list.sort(comparator);
            updateListDisplay();
            JOptionPane.showMessageDialog(this, "Сортировка выполнена успешно", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить список");
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                list.save(path);
                JOptionPane.showMessageDialog(this, "Список сохранён в: " + path, "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Загрузить список");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                CyclicList loaded = new CyclicList();
                loaded.load(path);
                list = loaded;
                updateListDisplay();
                JOptionPane.showMessageDialog(this, "Список загружен из: " + path, "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDebugMenu() {
        String[] options = {
                "Показать типы",
                "Тест Integer: добавление",
                "Тест Integer: вставка/удаление",
                "Тест SparseMatrix",
                "Тест сортировки",
                "Тест сериализации",
                "Закрыть"
        };

        int choice = JOptionPane.showOptionDialog(this,
                "Выберите тест отладки:",
                "Отладочное меню",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        try {
            switch (choice) {
                case 0 -> {
                    JOptionPane.showMessageDialog(this,
                            "Типы: " + factory.getTypeNameList(),
                            "Типы данных",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                case 1 -> {
                    CyclicList debugList = new CyclicList();
                    debugList.add(10);
                    debugList.add(20);
                    debugList.add(30);
                    JOptionPane.showMessageDialog(this,
                            "Integer debug: " + debugList,
                            "Тест Integer",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                case 2 -> {
                    CyclicList debugList = new CyclicList();
                    debugList.add(10);
                    debugList.add(20);
                    debugList.add(30);
                    debugList.insert(1, 99);
                    String msg = "После вставки: " + debugList + "\n";
                    msg += "Удалён: " + debugList.remove(1) + "\n";
                    msg += "После удаления: " + debugList;
                    JOptionPane.showMessageDialog(this,
                            msg,
                            "Тест вставки/удаления",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                case 3 -> {
                    SparseMatrix matrix = new SparseMatrix(3, 3);
                    matrix.setValue(0, 0, 5);
                    matrix.setValue(1, 2, 7);
                    CyclicList debugList = new CyclicList();
                    debugList.add(matrix);
                    JOptionPane.showMessageDialog(this,
                            "SparseMatrix debug: " + debugList,
                            "Тест SparseMatrix",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                case 4 -> {
                    CyclicList debugList = new CyclicList();
                    debugList.add(5);
                    debugList.add(1);
                    debugList.add(3);
                    debugList.add(2);
                    debugList.sort(factory.getBuilderByName("Integer").getTypeComparator());
                    JOptionPane.showMessageDialog(this,
                            "После сортировки: " + debugList,
                            "Тест сортировки",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                case 5 -> {
                    CyclicList debugList = new CyclicList();
                    debugList.add(15);
                    debugList.add(7);
                    debugList.add(9);
                    debugList.save("debug_integer_swing.txt");
                    CyclicList loaded = new CyclicList();
                    loaded.load("debug_integer_swing.txt");
                    String msg = "Исходный: " + debugList + "\n";
                    msg += "Загруженный: " + loaded;
                    JOptionPane.showMessageDialog(this,
                            msg,
                            "Тест сериализации",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка отладки: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTypeLabel() {
        typeLabel.setText("Текущий тип: " + currentType.typeName());
    }

    private void updateListDisplay() {
        if (list.isEmpty()) {
            listDisplay.setText("Список пуст");
        } else {
            listDisplay.setText(list.toString());
        }
    }

    public void show() {
        setVisible(true);
    }
}
